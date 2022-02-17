package com.example.weatherapp

import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import im.delight.android.location.SimpleLocation
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    var tvSicaklik: TextView? = null
    var tvAciklama: TextView? = null
    var tvTarih: TextView? = null
    var textView4: TextView? = null
    var rootLayout: RelativeLayout? = null
    var imgHavaDurumu: ImageView? = null
    var spnSehirler: Spinner? = null
    var spinnerTvSehir: TextView? = null
    var location: SimpleLocation? = null
    var latitude:String? = null
    var longitude:String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
       setContentView(R.layout.activity_main)

        implementViews()

        //spinner için adapter
        var spinnerAdapter = ArrayAdapter.createFromResource(
            this, R.array.sehirler, R.layout.spinner_tek_satir)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spnSehirler!!.adapter=spinnerAdapter

        // listener
        spnSehirler!!.setOnItemSelectedListener(this)

        spnSehirler!!.setSelection(1)
        verileriGetir("Konya")

    }

    // kullanıcının seçtiği şehri almak için bu event i kullanırız
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        spinnerTvSehir = view as TextView

        /*if(position == 0){

            location = SimpleLocation(this)

            if(!location!!.hasLocationEnabled()) {
                Toast.makeText(this, "GPS aç ki yerini bulalım", Toast.LENGTH_LONG).show()
                SimpleLocation.openSettings(this)
            } else {
                if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 60)
                }
                else{
                    location = SimpleLocation(this)
                    latitude = String().format("%.6f", location?.latitude)
                    longitude = String().format("%.6f", location?.longitude)

                    Log.e("LAT ", ""+latitude)
                    Log.e("LONG ", ""+longitude)

                    oankiSehirGetir(latitude,longitude)


                }

            }



        }else{
            var secilenSehir = parent?.getItemAtPosition(position).toString()
            verileriGetir(secilenSehir)
        }
         */

        var secilenSehir = parent?.getItemAtPosition(position).toString()
        verileriGetir(secilenSehir)
    }


    private fun oankiSehirGetir(lat:String?, longt:String?) {
        // weather api url https://home.openweathermap.org/users/sign_in
        val url = "https://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+longt+"&appid=ec488db2b75905a10250c2e2d89f00d5&lang=tr&units=metric"
        var sehirAdi:String? = null
        // Postman den en dıştaki json verisi obje mi array mi diye baktık
        // obje imiş. çünkü {} başlıyor. [] başlasaydı json array di.
        val havaDurumuObjeRequest2 = JsonObjectRequest(Request.Method.GET, url, null, object:Response.Listener<JSONObject> {
            // veri bulunursa buraya gelicek.
            override fun onResponse(response: JSONObject?) {
                // response içinde tüm json api si var.
                //Toast.makeText(this@MainActivity, response.toString(), Toast.LENGTH_LONG).show()

                // gerekli olan verileri response içinden çekip variable a atadık.
                var main = response?.getJSONObject("main")
                var sicaklik = main?.getInt("temp")
                tvSicaklik!!.text = sicaklik.toString()

                //Log.e("BEYZA", sicaklik.toString())

                sehirAdi = response?.getString("name")
                spinnerTvSehir?.setText(sehirAdi)

                var weather = response?.getJSONArray("weather")
                var aciklama = weather?.getJSONObject(0)?.getString("description")
                tvAciklama!!.text = aciklama?.uppercase()

                var icon = weather?.getJSONObject(0)?.getString("icon")  // 50n döner.


                if(icon?.last() == 'd'){
                    rootLayout!!.background = getDrawable(R.drawable.bg)
                    tvAciklama!!.setTextColor(Color.BLACK)
                    tvSicaklik!!.setTextColor(Color.BLACK)
                    tvTarih!!.setTextColor(Color.BLACK)
                    textView4!!.setTextColor(Color.BLACK) // derece işareti
                    spinnerTvSehir!!.setTextColor(Color.BLACK)
                    // spinner daki aşağı yönlü ok rengi, spinner_tek_satir.xml içinde de textColor değiştiriliyor."beyaz yaptım"
                    // Text color ı xml dosyasındaki id name i burda kullanarakta rengini değiştirebiliriz.
                    spnSehirler!!.background.setColorFilter(resources.getColor(R.color.black), PorterDuff.Mode.SRC_ATOP)
                } else {
                    // spinner daki aşağı yönlü ok rengi, spinner_tek_satir.xml içinde de textColor değiştiriliyor."beyaz yaptım"
                    // Text color ı xml dosyasındaki id name i burda kullanarakta rengini değiştirebiliriz. rootLayout!!.background = getDrawable(R.drawable.gece)
                    rootLayout!!.background = getDrawable(R.drawable.gece)
                    tvAciklama!!.setTextColor(Color.WHITE)
                    tvSicaklik!!.setTextColor(Color.WHITE)
                    tvTarih!!.setTextColor(Color.WHITE)
                    textView4!!.setTextColor(Color.WHITE) // derece işareti
                    spinnerTvSehir!!.setTextColor(Color.WHITE)
                    // spinner daki aşağı yönlü ok rengi, spinner_tek_satir.xml içinde de textColor değiştiriliyor."beyaz yaptım"
                    // Text color ı xml dosyasındaki id name i burda kullanarakta rengini değiştirebiliriz.
                    spnSehirler!!.background.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP)

                }

                // resimAdi = R.drawable.icon_50n
                var resimDosyaAdi = resources.getIdentifier("icon_" + icon?.sonKarakterSil(), "drawable", packageName)
                imgHavaDurumu!!.setImageResource(resimDosyaAdi)

                tvTarih!!.text = tarihYazdir()

                Log.e("BEYZAoankişehir", sicaklik.toString() + " " + sehirAdi + " " + aciklama + " " + icon)

            }

        }, object : Response.ErrorListener{
            override fun onErrorResponse(error: VolleyError?) {
                Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_LONG).show()
            }

        })

        MySingleton.getInstance(this)?.addToRequestQueue(havaDurumuObjeRequest2)

        // okyanustaysa kişi uygulama şehir adi veremiyor olabilir. if kontrolü
        // not found kısaltılması. şehir bulunamadıysa
        // Verileri çekeceğimiz api adresi :open weather sitesi.
        // kayıt olunması gerekli.
        // sehirAdi? -> "?" bu konulursa null da olabilir demek.
        // sehirAdi!! -> "!!" null olsun olmasın bana geri döndür demek.
    }

    // Bu event ilgili adapter kaybolduğunda çalışır. İşimize yaramaz.
    override fun onNothingSelected(p0: AdapterView<*>?) {}
/*
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        // kullanıcı location erişimi isteğine izin verdiyse
        if(requestCode == 60){
           if(grantResults.size > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
               location = SimpleLocation(this)
               latitude = String().format("%.6f", location?.latitude)
               longitude = String().format("%.6f", location?.longitude)

               Log.e("LAT ", ""+latitude)
               Log.e("LONG ", ""+longitude)

               oankiSehirGetir(latitude,longitude)
           }
        // kullanıcı location erişimi isteğine izin vermediyse
        }else{
            spnSehirler!!.setSelection(1)
            Toast.makeText(this, "İzin veredin de konumu bulaydık :P ", Toast.LENGTH_LONG).show()
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
*/
    fun verileriGetir(secilenSehir: String) {
        // istek atıyoruz, hangi adresten olduğunu belirtip,
        // response yani cevap olarak dönen adresteki api yi String olarak alıyoruz.
        // cevap dönüyor

        // weather api url
        val url = "https://api.openweathermap.org/data/2.5/weather?q="+secilenSehir+"&appid=ec488db2b75905a10250c2e2d89f00d5&lang=tr&units=metric"
        // Postman den en dıştaki json verisi obje mi array mi diye baktık
        // obje imiş. çünkü {} başlıyor. [] başlasaydı json array di.
        val havaDurumuObjeRequest = JsonObjectRequest(Request.Method.GET, url, null, object:Response.Listener<JSONObject> {
            // veri bulunursa buraya gelicek.
            override fun onResponse(response: JSONObject?) {
                // response içinde tüm json api si var.
                //Toast.makeText(this@MainActivity, response.toString(), Toast.LENGTH_LONG).show()

                // gerekli olan verileri response içinden çekip variable a atadık.
                var main = response?.getJSONObject("main")
                var sicaklik = main?.getInt("temp")
                tvSicaklik!!.text = sicaklik.toString()

                //Log.e("BEYZA", sicaklik.toString())

                var sehirAdi = response?.getString("name")

                var weather = response?.getJSONArray("weather")
                var aciklama = weather?.getJSONObject(0)?.getString("description")
                tvAciklama!!.text = aciklama?.uppercase()

                var icon = weather?.getJSONObject(0)?.getString("icon")  // 50n döner.


                if(icon?.last() == 'd'){
                    rootLayout!!.background = getDrawable(R.drawable.bg)
                    tvAciklama!!.setTextColor(Color.BLACK)
                    tvSicaklik!!.setTextColor(Color.BLACK)
                    tvTarih!!.setTextColor(Color.BLACK)
                    textView4!!.setTextColor(Color.BLACK) // derece işareti
                    spinnerTvSehir!!.setTextColor(Color.BLACK)
                    // spinner daki aşağı yönlü ok rengi, spinner_tek_satir.xml içinde de textColor değiştiriliyor."beyaz yaptım"
                    // Text color ı xml dosyasındaki id name i burda kullanarakta rengini değiştirebiliriz.
                    spnSehirler!!.background.setColorFilter(resources.getColor(R.color.black), PorterDuff.Mode.SRC_ATOP)
                } else {
                    // spinner daki aşağı yönlü ok rengi, spinner_tek_satir.xml içinde de textColor değiştiriliyor."beyaz yaptım"
                    // Text color ı xml dosyasındaki id name i burda kullanarakta rengini değiştirebiliriz. rootLayout!!.background = getDrawable(R.drawable.gece)
                    rootLayout!!.background = getDrawable(R.drawable.gece)
                    tvAciklama!!.setTextColor(Color.WHITE)
                    tvSicaklik!!.setTextColor(Color.WHITE)
                    tvTarih!!.setTextColor(Color.WHITE)
                    textView4!!.setTextColor(Color.WHITE) // derece işareti
                    spinnerTvSehir!!.setTextColor(Color.WHITE)
                    // spinner daki aşağı yönlü ok rengi, spinner_tek_satir.xml içinde de textColor değiştiriliyor."beyaz yaptım"
                    // Text color ı xml dosyasındaki id name i burda kullanarakta rengini değiştirebiliriz.
                    spnSehirler!!.background.setColorFilter(resources.getColor(R.color.white), PorterDuff.Mode.SRC_ATOP)

                }

                // resimAdi = R.drawable.icon_50n
                var resimDosyaAdi = resources.getIdentifier("icon_" + icon?.sonKarakterSil(), "drawable", packageName)
                imgHavaDurumu!!.setImageResource(resimDosyaAdi)

                tvTarih!!.text = tarihYazdir()

                Log.e("BEYZA2", sicaklik.toString() + " " + sehirAdi + " " + aciklama + " " + icon)

            }

        }, object : Response.ErrorListener{
            override fun onErrorResponse(error: VolleyError?) {
                Toast.makeText(this@MainActivity, "Error", Toast.LENGTH_LONG).show()
            }

        })

        MySingleton.getInstance(this)?.addToRequestQueue(havaDurumuObjeRequest)

        // Verileri çekeceğimiz api adresi :open weather sitesi.
        // kayıt olunması gerekli.
    }

    fun tarihYazdir() : String {

        val takvim = Calendar.getInstance().time
        val formatlayici = SimpleDateFormat("EEEE, MMMM yyyy", Locale("tr"))
        val tarih = formatlayici.format(takvim)

        return tarih
    }

    fun implementViews() {
        tvSicaklik = findViewById(R.id.tvSicaklik)
        tvAciklama = findViewById(R.id.tvAciklama)
        tvTarih = findViewById(R.id.tvTarih)
        textView4 = findViewById(R.id.textView4)
        rootLayout = findViewById(R.id.rootLayout)
        imgHavaDurumu = findViewById(R.id.imgHavaDurumu)
        spnSehirler = findViewById(R.id.spnSehirler)
    }

} //end class curly braces

private fun String.sonKarakterSil(): String {
    // 50n ifadesini 50 olarak geriye yollar.
    return this.substring(0, this.length-1)
}