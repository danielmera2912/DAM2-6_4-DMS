import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory


class CatalogoLibrosXML() {
    //directorio en formato string del archivo xml a leer
    var cargador: String = ""

    constructor(cargador: String) : this() {
        this.cargador = cargador
    }
    //función que se encarga de procesar el cargador en documento
    fun readXml(): Document? {
        try{
            val xmlFile = File(this.cargador)
            val xmlDoc: Document=  DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile)
            return xmlDoc
        }catch (e: Exception){
            return null
        }

    }
    //mediante esta función, que recibe un elemento devuelve un mapa de los atributos del elemento
    fun obtenerAtributosEnMapKV(e: Element ):MutableMap<String, String>
    {
        val mMap = mutableMapOf<String, String>()
        for(j in 0..e.attributes.length - 1)
            mMap.putIfAbsent(e.attributes.item(j).nodeName, e.attributes.item(j).nodeValue)
        return mMap
    }
    //mediante esta función, que recibe un elemento devuelve un mapa de los nodos del elemento
    fun obtenerNodosEnMapKV(e: Element ):MutableMap<String, String>
    {
        val mMap = mutableMapOf<String, String>()
        for(j in 0..e.childNodes.length - 1)
            mMap.putIfAbsent(e.childNodes.item(j).nodeName, e.childNodes.item(j).textContent)
        return mMap
    }
    //mediante esta función recibes los nodos según el tagname que se le pase por el constructor, para devolver la lista de nodos
    fun obtenerListaNodosPorNombre( tagName: String): MutableList<Node>
    {
        val xmlDoc: Document= readXml()!!
        val bookList: NodeList = xmlDoc.getElementsByTagName(tagName)
        val lista = mutableListOf<Node>()
        for(i in 0..bookList.length - 1)
            lista.add(bookList.item(i))
        return lista
    }
    //función que comprueba la existencia de un libro tras pasar la ID del libro
    fun existeLibro(idLibro: String): Boolean {
        //primero obtenemos la lista de nodos para guardarlo en la variable libros
        val libros = obtenerListaNodosPorNombre("book")
        var bol: Boolean= false
        var mMap:MutableMap<String, String> = mutableMapOf()
        libros.forEach{
            // se recorre la lista y se guarda en un mapa los atributos de cada elemento
            if (it.getNodeType() === Node.ELEMENT_NODE) {
                val elem = it as Element
                mMap = obtenerAtributosEnMapKV(it)
                // se comprueba que el valor del atributo de la lista de atributos de los nodos corresponde con la id del libro para verificar si el libro existe o no
                if(mMap.containsValue(idLibro)){
                    bol=true
                }
                return bol
            }
        }
        //devuelve true o false depende de si el mapa contiene o no dicha ID
        return mMap.containsValue(idLibro)
    }
    // devuelve la información completa del libro tras pasar su ID
    fun infoLibro (idLibro:String): Map<String, Any>? {
        val libros = obtenerListaNodosPorNombre("book")
        var mMap:MutableMap<String, String> = mutableMapOf()
        var mAtributos:MutableMap<String, String> = mutableMapOf()
        var mNodos:MutableMap<String, String> = mutableMapOf()
        libros.forEach{
            if (it.getNodeType() === Node.ELEMENT_NODE) {
                val elem = it as Element
                //guardamos los atributos y nodos de los elementos de la lista de nodos extraídos anteriormente
                mAtributos = obtenerAtributosEnMapKV(it)
                mNodos= obtenerNodosEnMapKV(it)
                //si coincidie el atributo con la ID del libro devuelve el mapa de atributos y nodos de dicho elemento
                if(mAtributos.containsValue(idLibro)){
                    return mAtributos+mNodos
                }

            }
        }
        return mMap
    }
}
fun main() {
    var ruta= "catalog.xml"
    var xmlDoc = CatalogoLibrosXML(ruta)
    xmlDoc.existeLibro("bk101")
    xmlDoc.infoLibro("bk101")

}