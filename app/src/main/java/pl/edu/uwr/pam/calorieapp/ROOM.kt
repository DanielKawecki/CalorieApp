package pl.edu.uwr.pam.calorieapp

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
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
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.sql.Date
import kotlin.math.roundToInt
import kotlin.math.roundToLong

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
    val date: String
)

data class DayCalorieSum(
    val date: String,
    val calorie: Int
)

@Entity(tableName = "meals")
data class Meal(
    @PrimaryKey(autoGenerate = true) val idm: Int,
    val name: String
)

@Entity(
    tableName = "meal_details",
    foreignKeys = [
        ForeignKey(
            entity = Meal::class,
            parentColumns = ["idm"],
            childColumns = ["m_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["m_id"])]
)
data class MealDetail(
    @PrimaryKey(autoGenerate = true) val idd: Int,
    @ColumnInfo(name = "m_id") val mealId: Int,
    val name: String,
    val amount: String,
    val calorie: Int,
    val protein: Double,
    val fats: Double,
    val carbs: Double,
)

data class NutrientSet(
    val calorie: Int,
    val protein: Double,
    val fats: Double,
    val carbs: Double
)

@Entity(tableName = "mass")
data class Mass(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val value: Double,
    val date: String
)

//  ██████   █████   ██████
//  ██   ██ ██   ██ ██    ██
//  ██   ██ ███████ ██    ██
//  ██   ██ ██   ██ ██    ██
//  ██████  ██   ██  ██████
//

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY date ASC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE DATE(date)=CURRENT_DATE ORDER BY date ASC")
    fun getTodayProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :productId")
    fun getProductById(productId: Int): Flow<Product>

    @Query("""
        SELECT 
            COALESCE(SUM(calorie), 0) AS calorie, 
            COALESCE(SUM(protein), 0.0) AS protein, 
            COALESCE(SUM(fats), 0.0) AS fats, 
            COALESCE(SUM(carbs), 0.0) AS carbs 
        FROM products WHERE DATE(date)=CURRENT_DATE""")
    fun getNutrientsSum(): Flow<NutrientSet>

    @Query("SELECT DATE(date) as date, SUM(calorie) AS calorie FROM products GROUP BY DATE(date)")
    fun getCalorieSumByDate(): Flow<List<DayCalorieSum>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOLD(product: Product)

    @Query("""INSERT INTO products (name, amount, meal, calorie, protein, fats, carbs, date) 
        VALUES (:name, :amount, :meal, :calorie, :protein, :fats, :carbs, CURRENT_TIMESTAMP)""")
    suspend fun insert(name: String, amount: String, meal: String, calorie: Int, protein: Double, fats: Double, carbs: Double)

    @Query("""INSERT INTO products (name, amount, meal, calorie, protein, fats, carbs, date) 
        VALUES ("Sample", "100g", "Breakfast", :calorie, 12.0, 6.0, 4.0, :date)""")
    suspend fun insertSample(calorie: Int, date: String)

    @Query("UPDATE products SET name=:name, amount=:amount, calorie=:calorie, protein=:protein, fats=:fats, carbs=:carbs WHERE id = :id")
    suspend fun updateProductById(id: Int, name: String, amount: String, calorie: Int, protein: Double, fats: Double, carbs: Double)

    @Query("DELETE FROM products WHERE id = :productId")
    suspend fun deleteProductById(productId: Int)

    @Query("DELETE FROM products")
    suspend fun deleteAll()
}

@Dao
interface MealDao {
    @Query("SELECT * FROM meals ORDER BY name")
    fun getAllMeals(): Flow<List<Meal>>

    @Query("UPDATE meals SET name=:name WHERE idm=:id")
    suspend fun updateMealByID(id: Int, name: String)

    @Query("SELECT name FROM meals WHERE idm=:id")
    suspend fun getMealNameByID(id: Int): String

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: Meal): Long  // Returns inserted meal ID

    @Query("DELETE FROM meals WHERE idm = :id")
    suspend fun deleteMealById(id: Int)
}

@Dao
interface MealDetailDao {
    @Query("SELECT * FROM meal_details WHERE m_id = :mealId")
    fun getMealDetails(mealId: Int): Flow<List<MealDetail>>

    @Query("SELECT COUNT(*) FROM meal_details WHERE m_id = :mealId")
    suspend fun getDetailCount(mealId: Int): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealDetail(mealDetail: MealDetail)

    @Query("DELETE FROM meal_details WHERE idd = :id")
    suspend fun deleteMealDetailId(id: Int)
}

@Dao
interface MassDao {
    @Query("SELECT * FROM mass ORDER BY date ASC")
    fun getAllMasses(): Flow<List<Mass>>

    @Query("SELECT * FROM mass WHERE DATE(date) = DATE(CURRENT_TIMESTAMP)")
    fun getTodayMass(): Flow<Mass>

    @Query("INSERT INTO mass (value, date) VALUES (:value, CURRENT_TIMESTAMP)")
    suspend fun insert(value: Double)

    @Query("INSERT INTO mass (value, date) VALUES (:value, :date)")
    suspend fun insertSample(value: Double, date: String)

    @Query("UPDATE mass SET value=:value WHERE id = :id")
    suspend fun updateMassById(id: Int, value: Double)

    @Query("DELETE FROM mass WHERE id = :id")
    suspend fun deleteMassById(id: Int)

    @Query("DELETE FROM mass")
    suspend fun deleteAll()
}

@Database(entities = [Product::class, Meal::class, MealDetail::class, Mass::class], version = 1, exportSchema = false)
abstract class ProductDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun mealDao(): MealDao
    abstract fun mealDetailDao(): MealDetailDao
    abstract fun massDao(): MassDao

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

//  ██████  ███████ ██████   ██████  ███████ ██ ████████  ██████  ██████  ██    ██
//  ██   ██ ██      ██   ██ ██    ██ ██      ██    ██    ██    ██ ██   ██  ██  ██
//  ██████  █████   ██████  ██    ██ ███████ ██    ██    ██    ██ ██████    ████
//  ██   ██ ██      ██      ██    ██      ██ ██    ██    ██    ██ ██   ██    ██
//  ██   ██ ███████ ██       ██████  ███████ ██    ██     ██████  ██   ██    ██
//

class ProductRepository(
    private val productDao: ProductDao,
    private val mealDao: MealDao,
    private val mealDetailDao: MealDetailDao,
    private val massDao: MassDao,
    application: Application)
{
    private val api = RetrofitInstance.api
    val sharedPreferences = application.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)

    private var _budget: Int = sharedPreferences.getInt("calorie_budget", 100)
    val budget: Int
        get() = _budget

    //    --------------------- PRODUCT DAO ------------------------

    fun getAllProducts() = productDao.getAllProducts()
    fun getTodayProducts() = productDao.getTodayProducts()
    fun getProductById(productId: Int) = productDao.getProductById(productId)
    fun getNutrientsSum() = productDao.getNutrientsSum()
    fun getCalorieSumByDate() = productDao.getCalorieSumByDate()

    suspend fun updateProductById(productId: Int, name: String, amount: String) {
        val response = api.getNutrition("$amount of $name")
        val calorie = response.items.sumOf { it.calories }
        val protein = response.items.sumOf { it.protein_g }
        val fats = response.items.sumOf { it.fat_total_g }
        val carbs = response.items.sumOf { it.carbohydrates_total_g }
        productDao.updateProductById(productId, name, amount, calorie.toInt(), protein, fats, carbs)
    }

    suspend fun deleteProductById(productId: Int) = productDao.deleteProductById(productId)
    suspend fun deleteAllProducts() = productDao.deleteAll()

    suspend fun getNutrition(query: String): NutritionResponse {
        return api.getNutrition(query)
    }

    suspend fun addWithNutrition(name: String, amount: String, meal: String) {
        val response = api.getNutrition("$amount of $name")
        val calorie = response.items.sumOf { it.calories }
        val protein = response.items.sumOf { it.protein_g }
        val fats = response.items.sumOf { it.fat_total_g }
        val carbs = response.items.sumOf { it.carbohydrates_total_g }
        productDao.insert(name, amount, meal, calorie.toInt(), protein, fats, carbs)
    }

    suspend fun addProduct(name: String, amount: String, meal: String, calorie: Int, protein: Double, fats: Double, carbs: Double) {
        productDao.insert(name, amount, meal, calorie, protein, fats, carbs)
    }

    suspend fun addSampleProduct(calorie: Int, date: String) {
        productDao.insertSample(calorie, date)
    }

    //    --------------------- MEAL DAO ------------------------

    fun getAllCustomMeals() = mealDao.getAllMeals()
    suspend fun updateMealNameByID(id: Int, name: String) = mealDao.updateMealByID(id, name)
    suspend fun getMealNameByID(id: Int) = mealDao.getMealNameByID(id)
    suspend fun addCustomMeal(meal: Meal) = mealDao.insertMeal(meal)
    suspend fun deleteCustomMealById(id: Int) = mealDao.deleteMealById(id)

    //    --------------------- MEAL DETAIL DAO ------------------------

    fun getCustomMealDetails(id: Int) = mealDetailDao.getMealDetails(id)

    suspend fun getDetailCount(id: Int) = mealDetailDao.getDetailCount(id)

    suspend fun addMealDetails(id: Int,  name: String, amount: String) {
        val response = api.getNutrition("$amount of $name")
        val calorie = response.items.sumOf { it.calories }
        val protein = response.items.sumOf { it.protein_g }
        val fats = response.items.sumOf { it.fat_total_g }
        val carbs = response.items.sumOf { it.carbohydrates_total_g }
        mealDetailDao.insertMealDetail(MealDetail(0, id, name, amount, calorie.toInt(), protein, fats, carbs))
    }

    suspend fun deleteMealDetailById(id: Int) = mealDetailDao.deleteMealDetailId(id)

    //    --------------------- MASS DAO ------------------------

    fun getAllMasses() = massDao.getAllMasses()
    fun getTodayMass() = massDao.getTodayMass()
    suspend fun addMass(value: Double) = massDao.insert(value)
    suspend fun addSampleMass(value: Double, date: String) = massDao.insertSample(value, date)
    suspend fun updateMassById(id: Int, value: Double) = massDao.updateMassById(id, value)
    suspend fun deleteMassById(id: Int) = massDao.deleteMassById(id)
    suspend fun deleteAllMass() = massDao.deleteAll()

    //    --------------------- SHARED PREFERENCES ------------------------

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

//  ██    ██ ███    ███  ██████  ██████  ███████ ██
//  ██    ██ ████  ████ ██    ██ ██   ██ ██      ██
//  ██    ██ ██ ████ ██ ██    ██ ██   ██ █████   ██
//   ██  ██  ██  ██  ██ ██    ██ ██   ██ ██      ██
//    ████   ██      ██  ██████  ██████  ███████ ███████
//

class ProductViewModel(application: Application) : ViewModel() {

    private val repository: ProductRepository

    private val _productState = MutableStateFlow<List<Product>>(emptyList())
    val productState: StateFlow<List<Product>>
        get() = _productState

    private val _todayProductState = MutableStateFlow<List<Product>>(emptyList())
    val todayProductState: StateFlow<List<Product>>
        get() = _todayProductState

    private val _nutrientsSum = MutableStateFlow(NutrientSet(0, 0.0, 0.0, 0.0))
    val nutrientsSum: StateFlow<NutrientSet>
        get() = _nutrientsSum

    private val _calorieSumByDate = MutableStateFlow<List<DayCalorieSum>>(emptyList())
    val calorieSumByDate: StateFlow<List<DayCalorieSum>>
        get() = _calorieSumByDate

    private val _customMeals = MutableStateFlow<List<Meal>>(emptyList())
    val customMeals: StateFlow<List<Meal>>
        get() = _customMeals

    private val _mealDetails = MutableStateFlow<List<MealDetail>>(emptyList())
    val mealDetails: StateFlow<List<MealDetail>>
        get() = _mealDetails

    private val _budget: MutableStateFlow<Int> = MutableStateFlow(0)
    val budget: StateFlow<Int>
        get() = _budget

    private val _masses = MutableStateFlow<List<Mass>>(emptyList())
    val massesState: StateFlow<List<Mass>>
        get() = _masses

    private val _todayMass = MutableStateFlow(Mass(0, -1.0, ""))
    val todayMassState: StateFlow<Mass>
        get() = _todayMass

    init {
        val db = ProductDatabase.getDatabase(application)
        val productDao = db.productDao()
        val mealDao = db.mealDao()
        val mealDetailDao = db.mealDetailDao()
        val massDao = db.massDao()
        repository = ProductRepository(productDao, mealDao, mealDetailDao, massDao, application)

        _budget.value = repository.budget

        fetchProducts()
        fetchTodayProducts()
        fetchNutrientsSum()
        fetchCustomMeals()
        fetchMasses()
        fetchTodayMass()
        fetchDayCalorieSum()
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

    private fun fetchNutrientsSum() {
        viewModelScope.launch {
            repository.getNutrientsSum().collect { users ->
                _nutrientsSum.value = users
            }
        }
    }

    private fun fetchDayCalorieSum() {
        viewModelScope.launch {
            repository.getCalorieSumByDate().collect { sum ->
                _calorieSumByDate.value = sum
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

    fun updateProductById(productId: Int, name: String, amount: String) {
        viewModelScope.launch {
            repository.updateProductById(productId, name, amount)
        }
    }

    fun deleteProductById(productId: Int) {
        viewModelScope.launch {
            repository.deleteProductById(productId)
        }
    }

    fun clearAllProducts() {
        viewModelScope.launch {
            repository.deleteAllProducts()
        }
    }

    fun addWithNutrition(name: String, amount: String, meal: String) {
        viewModelScope.launch {
            repository.addWithNutrition(name, amount, meal)
        }
    }

    fun addProduct(name: String, amount: String, meal: String, calorie: Int, protein: Double, fats: Double, carbs: Double) {
        viewModelScope.launch {
            repository.addProduct(name, amount, meal, calorie, protein, fats, carbs)
        }
    }

    fun addSampleProduct(calorie: Int, date: String) {
        viewModelScope.launch {
            repository.addSampleProduct(calorie, date)
        }
    }

    // ---------------- Meal Functions ------------------

    fun addCustomMeal(name: String) {
        viewModelScope.launch {
            repository.addCustomMeal(Meal(0, name))
        }
    }

    fun updateMealByID(id: Int, name: String) {
        viewModelScope.launch {
            repository.updateMealNameByID(id, name)
        }
    }

    private fun fetchCustomMeals() {
        viewModelScope.launch {
            repository.getAllCustomMeals().collect { users ->
                _customMeals.value = users
            }
        }
    }

    fun deleteCustomMealById(id: Int) {
        viewModelScope.launch {
            repository.deleteCustomMealById(id)
        }
    }

    // ---------------- Meal Detail Functions ------------------

    fun getMealDetail(id: Int) {
        viewModelScope.launch {
            repository.getCustomMealDetails(id).collect { users ->
                _mealDetails.value = users
            }
        }
    }

    suspend fun getDetailCount(id: Int): Int {
        return repository.getDetailCount(id)
    }

    fun addMealDetail(id: Int, name: String, amount: String) {
        viewModelScope.launch {
            repository.addMealDetails(id, name, amount)
        }
    }

    fun deleteMealDetailById(id: Int) {
        viewModelScope.launch {
            repository.deleteMealDetailById(id)
        }
    }

    fun addMealToProducts(id: Int, meal: String) {
//        viewModelScope.launch {
//            val details = repository.getCustomMealDetails(id).first()
//
//            if (details.isNotEmpty()) {
//                val totalCalories = details.sumOf { it.calorie }
//                val totalProtein = details.sumOf { it.protein }.round1()
//                val totalFats = details.sumOf { it.fats }.round1()
//                val totalCarbs = details.sumOf { it.carbs }.round1()
//                val totalAmount = details.sumOf { it.amount.filter { !it.isDigit() } }
//                val name = repository.getMealNameByID(id)
//
//                repository.addProduct(name, "g", meal, totalCalories, totalProtein, totalFats, totalCarbs)
//            }
//            details.forEach { d ->
//                repository.addProduct(d.name, "change this", meal, d.calorie, d.protein, d.fats, d.carbs)
//            }
//        }
        viewModelScope.launch {
            val details = repository.getCustomMealDetails(id).first()
            details.forEach { d ->
                repository.addProduct(d.name, d.amount, meal, d.calorie, d.protein, d.fats, d.carbs)
            }
        }
    }

    // ---------------- Mass Functions ------------------

    private fun fetchMasses() {
        viewModelScope.launch {
            repository.getAllMasses().collect { mass ->
                _masses.value = mass
            }
        }
    }

    private fun fetchTodayMass() {
        viewModelScope.launch {
            repository.getTodayMass().collect { mass ->
                _todayMass.value = mass
            }
        }
    }

    fun addMass(value: Double) {
        viewModelScope.launch {
            repository.addMass(value)
        }
    }

    fun addSampleMass(value: Double, date: String) {
        viewModelScope.launch {
            repository.addSampleMass(value, date)
        }
    }

    fun updateMassById(id: Int, value: Double) {
        viewModelScope.launch {
            repository.updateMassById(id, value)
        }
    }

    fun deleteMassById(id: Int) {
        viewModelScope.launch {
            repository.deleteMassById(id)
        }
    }

    fun clearAllMass() {
        viewModelScope.launch {
            repository.deleteAllMass()
        }
    }

    // ---------------- Retrofit fetch ------------------

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