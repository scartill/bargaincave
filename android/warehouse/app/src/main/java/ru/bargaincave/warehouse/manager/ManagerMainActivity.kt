package ru.bargaincave.warehouse.manager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.AWSDataStorePlugin
import com.amplifyframework.datastore.generated.model.Lot
import ru.bargaincave.warehouse.databinding.ActivityManagerMainBinding
import androidx.recyclerview.widget.LinearLayoutManager




class ManagerMainActivity : AppCompatActivity() {
    private lateinit var b: ActivityManagerMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        b = ActivityManagerMainBinding.inflate(layoutInflater)
        val view = b.root
        setContentView(view)

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        b.lotList.layoutManager = linearLayoutManager
        b.lotList.adapter = LotListAdapter()

        /*
        b.btnLoad.setOnClickListener {
            Amplify.DataStore.query(
                Lot::class.java,
                Where.matches(
                  Lot.FRUIT.eq(Fruit.MANGO)
                ),
                { lots ->
                    while (lots.hasNext()) {
                        val lot = lots.next()
                        val comment: String? = lot.comment

                        Log.i("Cave", "==== Lot ====")
                        Log.i("Cave", "Fruit: ${lot.fruit}")
                        Log.i("Cave", "Weight: ${lot.weightKg}")

                        if (comment != null) {
                            Log.i("Cave", "Comment: $comment")
                        }
                    }
                },
                { failure -> Log.e("Cave", "Could not query DataStore", failure) }
            )
        }
         */

        /*
                // TODO: move to after-login code
                Amplify.DataStore.observe(
                    Lot::class.java,
                    { Log.i("Cave", "Observation began.") },
                    { Log.i("Cave", it.item().toString()) },
                    { Log.e("Cave", "Observation failed.", it) },
                    { Log.i("Cave", "Observation complete.") }
                )
        */
    }
}