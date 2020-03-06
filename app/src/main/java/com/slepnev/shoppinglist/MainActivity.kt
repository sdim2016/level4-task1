package com.slepnev.shoppinglist

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var productRepository: ProductRepository
    private val products = arrayListOf<Product>()
    private val productAdapter = ProductAdapter(products)
    private val mainScope = CoroutineScope(Dispatchers.Main)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        productRepository = ProductRepository(this)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initViews() {
        rvList.layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
        rvList.adapter = productAdapter
        rvList.addItemDecoration(DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL))

        getShoppingListFromDatabase()
    }

    private fun getShoppingListFromDatabase() {
        mainScope.launch {
            val shoppingList = withContext(Dispatchers.IO) {
                productRepository.getAllProducts()
            }
            this@MainActivity.products.clear()
            this@MainActivity.products.addAll(shoppingList)
            this@MainActivity.productAdapter.notifyDataSetChanged()
        }
    }

    private fun validateFields(): Boolean {
        return if (etCount.text.toString().isNotBlank() && etProduct.text.toString().isNotBlank()) {
            true
        } else {
            Toast.makeText(this, "Please fill in the fields!", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun addProduct() {
        if (validateFields()) {
            mainScope.launch {
                val product = Product (
                    name = etProduct.text.toString(),
                    quantity = etProduct.text.toString().toInt()
                )

                withContext(Dispatchers.IO) {
                   productRepository.insertProduct(product)
                }

                getShoppingListFromDatabase()
            }
        }
    }
}
