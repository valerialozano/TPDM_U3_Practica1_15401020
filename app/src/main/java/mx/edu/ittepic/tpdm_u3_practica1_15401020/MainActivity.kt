package mx.edu.ittepic.tpdm_u3_practica1_15401020

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    var descripcion : EditText?=null
    var monto : EditText?=null
    var fecha : EditText?=null
    var pagado : CheckBox?=null
    var insertar : Button?= null
    var listView : ListView?=null

    var baseRemota = FirebaseFirestore.getInstance()
    var registros = ArrayList<String>()
    var keys = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        descripcion = findViewById(R.id.descripcion)
        monto = findViewById(R.id.monto)
        fecha = findViewById(R.id.fecha)
        pagado = findViewById(R.id.pagado)
        insertar = findViewById(R.id.insertar)
        listView = findViewById(R.id.listView)

        var p =false
        pagado?.setOnCheckedChangeListener { buttonView, isChecked ->
            if(pagado?.isChecked==true){
                p=true
            }else{
                p=false
            }
        }
        insertar?.setOnClickListener {
            if(descripcion?.text.toString().isEmpty()|| monto?.text.toString().isEmpty() || fecha?.text.toString().isEmpty()){
                mensaje("¡Error!","Por favor, llena todos los campos.")
                return@setOnClickListener
            }
            var datosInsertar = hashMapOf(
                "descripcion" to descripcion?.text.toString(),
                "monto" to monto?.text.toString().toDouble(),
                "fecha" to fecha?.text.toString(),
                "pagado" to p
            )
            baseRemota.collection("recibopagos").add(datosInsertar as Map<String,Any>)
                .addOnSuccessListener {
                    mensaje("Éxito", "El pago se registró correctamente.")
                    limpiarCampos()
                }
                .addOnFailureListener {
                    mensaje("¡Error!","No se pudo registrar el pago.")
                    limpiarCampos()
                }
        }
        baseRemota.collection("recibopagos").addSnapshotListener { querySnapshot, e ->
            if(e!=null){
                mensaje("¡Error!","No hay acceso a los datos.")
            }
            registros.clear()
            keys.clear()
            for(document in querySnapshot!!){
                var pa=""
                if(document.getBoolean("pagado")==true){
                    pa="Pagado"
                }else{
                    pa="Pago pendiente"
                }
                var cadena = "${document.getString("descripcion")}\n ${document.getDouble("monto").toString()}  ${document.getString("fecha")}\n $pa"
                registros.add(cadena)
                keys.add(document.id)
            }
            var adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,registros)
            listView?.adapter = adapter
        }
        listView?.setOnItemClickListener { parent, view, position, id ->
            var id = keys.get(position)
            var activity = Intent(this, Main2Activity::class.java)
            activity.putExtra("id",id)
            startActivity(activity)
        }

    }
    fun mensaje(titulo:String, mensaje:String){
        AlertDialog.Builder(this).setTitle(titulo).setMessage(mensaje).setPositiveButton("Aceptar"){ dialog, which->}.show()
    }
    fun limpiarCampos(){
        descripcion?.setText("")
        monto?.setText("")
        fecha?.setText("")
        pagado?.setChecked(false)
    }
}
