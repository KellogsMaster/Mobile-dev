package fr.isen.delesse.androidrestaurant.category

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.GsonBuilder
import fr.isen.delesse.androidrestaurant.HomeActivity
import fr.isen.delesse.androidrestaurant.R
import fr.isen.delesse.androidrestaurant.databinding.ActivityCategoryBinding
import fr.isen.delesse.androidrestaurant.network.Category
import fr.isen.delesse.androidrestaurant.network.Dish
import fr.isen.delesse.androidrestaurant.network.MenuResult
import fr.isen.delesse.androidrestaurant.network.NetworkConstant
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener

class CategoryActivity : AppCompatActivity() {

    enum class ItemType {
        ENTREES, PLATS, DESSERTS
    }

    private lateinit var binding : ActivityCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("CategoryActivity", "start of CategoryActivity")
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        //setContentView(R.layout.activity_category)
        setContentView(binding.root)
        val selectedItem = intent.getSerializableExtra(HomeActivity.CATEGORY_NAME) as? ItemType

        val queue = Volley.newRequestQueue(this)
        val url = NetworkConstant.BASE_URL + NetworkConstant.PATH_MENU

        val jsonData = JSONObject()
        jsonData.put(NetworkConstant.ID_SHOP,1)
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST,
            url,
            jsonData,
            { response ->
                val menuResult = GsonBuilder().create().fromJson(response.toString(), MenuResult::class.java)
                var item = menuResult.data.firstOrNull { it.name == "Entrées" }
                loadList(selectedItem, item.getAllDishName())
            },
            { error ->
                error.message?.let {
                    Log.d("request error : ", it)
                } ?: run {
                    Log.d("request : ", error.toString())
                }

            })

        queue.add(jsonObjectRequest)


        binding.categoryTitle.text = getCategoryTitle(selectedItem)

        //loadList(selectedItem)
    }
    private fun loadList(item: ItemType?, list: List<String>) {

        var entrees = listOf<String>("salade", "poêle de legume", "fruits de mer")
        var plats = listOf<String>("gratin", "pizza", "pâtes")
        var desserts = listOf<String>("gateau au chocolat", "salade de fruits", "glace")
        var entries = listOf<String>()
        when (item) {
            ItemType.ENTREES -> entries = entrees
            ItemType.PLATS -> entries = plats
            ItemType.DESSERTS -> entries = desserts
        }
        val adapter = CategoryAdapter(list)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }
    private fun getCategoryTitle( item: ItemType?): String {
        return when(item) {
            ItemType.ENTREES -> getString(
                R.string.app_entree
            )
            ItemType.PLATS -> getString(
                R.string.app_plat
            )
            ItemType.DESSERTS -> getString(
                R.string.app_dessert
            )
            else -> ""
        }
    }

    override fun onDestroy() {
        Log.d("CategoryActivity", "end of CategoryActivity")
        super.onDestroy()
    }
}