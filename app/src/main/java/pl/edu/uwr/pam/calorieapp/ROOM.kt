package pl.edu.uwr.pam.calorieapp

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.sql.Date

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val amount: String,
    val meal: String,
    val calorie: Int,
    val protein: Double,
    val fats: Double,
    val carbs: Double,
//    @ColumnInfo(defaultValue = "CURRENT_DATE")
    val date: String
)

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY date ASC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE DATE(date)=CURRENT_DATE ORDER BY date ASC")
    fun getTodayProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :productId")
    fun getProductById(productId: Int): Flow<Product>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOLD(product: Product)

    @Query("""INSERT INTO products (name, amount, meal, calorie, protein, fats, carbs, date) 
        VALUES (:name, :amount, :meal, :calorie, :protein, :fats, :carbs, CURRENT_TIMESTAMP)""")
    suspend fun insert(name: String, amount: String, meal: String, calorie: Int, protein: Double, fats: Double, carbs: Double)

    @Query("UPDATE products SET name = :name, calorie = :calorie WHERE id = :id")
    suspend fun updateProductById(id: Int, name: String, calorie: Int)

    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteProductById(productId: Int)

    @Query("DELETE FROM products")
    suspend fun deleteAll()
}

@Database(entities = [Product::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ProductDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var Instance: ProductDatabase? = null

        fun getDatabase(context: Context): ProductDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, ProductDatabase::class.java, "product_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}

class ProductRepository(private val productDao: ProductDao, application: Application) {
    private val api = RetrofitInstance.api
    val sharedPreferences = application.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)

    private var _budget: Int = sharedPreferences.getInt("calorie_budget", 100)
    val budget: Int
        get() = _budget

    fun getAllProducts() = productDao.getAllProducts()
    fun getTodayProducts() = productDao.getTodayProducts()
    fun getProductById(productId: Int) = productDao.getProductById(productId)
    suspend fun updateProductById(productId: Int, name: String, calorie: Int) =
        productDao.updateProductById(productId, name, calorie)
    suspend fun deleteProductById(productId: Int) = productDao.deleteProductById(productId)
    suspend fun clear() = productDao.deleteAll()

    suspend fun getNutrition(query: String): NutritionResponse {
        return api.getNutrition(query)
    }

    suspend fun addWithNutrition(name: String, amount: String, meal: String, date: Date) {
        val response = api.getNutrition("$amount of $name")
        val calorie = response.items.sumOf { it.calories }
        val protein = response.items.sumOf { it.protein_g }
        val fats = response.items.sumOf { it.fat_total_g }
        val carbs = response.items.sumOf { it.carbohydrates_total_g }
//        productDao.insertOLD(Product(0, name, 1.0f, meal, calorie.toInt(), protein, fats, carbs, date))
        println("Dziala")
        productDao.insert(name, amount, meal, calorie.toInt(), protein, fats, carbs)
    }

    fun setBudget(calorieBudget: Int) {
        val edit = sharedPreferences.edit()
        edit.putInt("calorie_budget", calorieBudget).apply()
        _budget = calorieBudget
    }
}

class ProductViewModelFactory(val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProductViewModel(application) as T
    }
}

class ProductViewModel(application: Application) : ViewModel() {

    private val repository: ProductRepository

    private val _productState = MutableStateFlow<List<Product>>(emptyList())
    val productState: StateFlow<List<Product>>
        get() = _productState

    private val _todayProductState = MutableStateFlow<List<Product>>(emptyList())
    val todayProductState: StateFlow<List<Product>>
        get() = _todayProductState

    private val _budget: MutableStateFlow<Int> = MutableStateFlow(0)
    val budget: StateFlow<Int>
        get() = _budget

    init {
        val db = ProductDatabase.getDatabase(application)
        val dao = db.productDao()
        repository = ProductRepository(dao, application)

        _budget.value = repository.budget

        fetchProducts()
        fetchTodayProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            repository.getAllProducts().collect { users ->
                _productState.value = users
            }
        }
    }

    private fun fetchTodayProducts() {
        viewModelScope.launch {
            repository.getTodayProducts().collect { users ->
                _todayProductState.value = users
            }
        }
    }

    fun getProductById(productId: Int): StateFlow<Product?> {
        val productFlow = MutableStateFlow<Product?>(null)

        viewModelScope.launch {
            repository.getProductById(productId).collect {
                    product -> productFlow.value = product
            }
        }

        return productFlow
    }

    fun updateProductById(productId: Int, name: String, calorie: Int) {
        viewModelScope.launch {
            repository.updateProductById(productId, name, calorie)
        }
    }

    fun deleteProductById(productId: Int) {
        viewModelScope.launch {
            repository.deleteProductById(productId)
        }
    }

    fun clearProducts() {
        viewModelScope.launch {
            repository.clear()
        }
    }

    fun addWithNutrition(name: String, amount: String, meal: String, date: Date) {
        viewModelScope.launch {
            repository.addWithNutrition(name, amount, meal, date)
        }
    }

    // Retrofit fetch
    fun getNutrition(query: String): SharedFlow<NutritionResponse?> {
        val nutritionFlow = MutableSharedFlow<NutritionResponse?>()

        viewModelScope.launch {
            nutritionFlow.emit(repository.getNutrition(query))
        }
        return nutritionFlow
    }

    // SharedPreferences
    fun setCalorieBudget(calorieBudget: Int) {
        repository.setBudget(calorieBudget)
        _budget.value = calorieBudget
    }
}