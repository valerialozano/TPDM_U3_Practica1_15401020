package mx.edu.ittepic.tpdm_u3_practica1_15401020

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore

class Main2Activity : AppCompatActivity() {

    var descripcion2 : EditText?=null
    var monto2 : EditText?=null
    var fecha2 : EditText?=null
    var pagado2 : CheckBox?=null
    var actualizar : Button?= null
    var eliminar : Button?= null
    var regresar : Button?= null

    var baseRemota = FirebaseFirestore.getInstance()
    var id =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        descripcion2 = findViewById(R.id.descripcion2)
        monto2 = findViewById(R.id.monto2)
        fecha2 = findViewById(R.id.fecha2)
        pagado2 = findViewById(R.id.pagado2)
        actualizar = findViewById(R.id.actualizar)
        eliminar = findViewById(R.id.eliminar)
        regresar = findViewById(R.id.regresar)

        id = intent.extras?.getString("id").toString()

        var p = false
        pagado2?.setOnCheckedChangeListener { buttonView, isChecked ->
            if(pagado2?.isChecked==true){
                p=true
            }else{
                p=false
            }
        }
        baseRemota.collection("recibopagos").document(id).get()
            .addOnSuccessListener {
                descripcion2?.setText(it.getString("descripcion"))
                monto2?.setText(it.getDouble("monto").toString())
                fecha2?.setText(it.getString("fecha"))
                if(it.getBoolean("pagado")==true){
                    pagado2?.setChecked(true)
                }else{
                    pagado2?.setChecked(false)
                }
            }
            .addOnFailureListener {
                mensaje("¡Error!","No hay datos.")
            }
        actualizar?.setOnClickListener {
            if(descripcion2?.text.toString().isEmpty()|| monto2?.text.toString().isEmpty() || fecha2?.text.toString().isEmpty()){
                mensaje("¡Error!","Por favor, llena todos los campos.")
                return@setOnClickListener
            }
            var datosActualizar = hashMapOf(
                "descripcion" to descripcion2?.text.toString(),
                "monto" to monto2?.text.toString().toDouble(),
                "fecha" to fecha2?.text.toString(),
                "pagado" to p
            )
            baseRemota.collection("recibopagos").document(id).set(datosActualizar as Map<String,Any>)
                .addOnSuccessListener {
                    mensaje("Éxito","Se actualizó el pago correctamente.")
                    limpiarCampos()
                }
                .addOnFailureListener {
                    mensaje("¡Error!","No se pudo actualizar el pago.")
                    limpiarCampos()
                }
        }
        eliminar?.setOnClickListener {
            baseRemota.collection("recibopagos").document(id).delete()
                .addOnSuccessListener {
                    mensaje("Éxito","Se eliminó el pago correctamente.")
                    limpiarCampos()
                }
                .addOnFailureListener {
                    mensaje("¡Error!","No se pudo eliminar el pago.")
                    limpiarCampos()
                }
        }
        regresar?.setOnClickListener {
            finish()
        }

    }
    fun mensaje(titulo:String, mensaje:String){
        AlertDialog.Builder(this).setTitle(titulo).setMessage(mensaje).setPositiveButton("Aceptar"){ dialog, which->}.show()
    }
    fun limpiarCampos(){
        descripcion2?.setText("")
        monto2?.setText("")
        fecha2?.setText("")
        pagado2?.setChecked(false)
    }
}
