#!/usr/bin/python3
import re, time, requests,threading
from os import mkdir,chdir,makedirs,system
from os.path import abspath,dirname,basename,realpath
from bs4 import BeautifulSoup
from urllib.parse import urlsplit
from urllib.request import urlopen,urlretrieve
from concurrent.futures import ThreadPoolExecutor


def TipoContenido(sitio):
	try:
		r = requests.get(sitio)
		return r.headers['content-type']
	except Exception as e:		#Este problema puede ocurrir al descargar un archivo que no tenga extensión
		print("[W/TIPOCONT] Hay algo raro con este enlace! (",sitio,")",sep="")
		return "unknown"		#Regresar algo por lo menos

def CodificacionSitio(sitio):	#Esta función se encarga de obtener el tipo de codificación en la cual está el sitio
	try:	return requests.get(sitio).encoding	#Ya sea utf-8, o ISO, por nombrar
	except Exception as e:	print("[ERROR/codificación] Houston, tenemos problemas! (",e,")",sep="")

def DescargarSitio(sitio,nombre):
	try:
		urlab = urlopen(sitio).url
		urlretrieve(urlab,nombre+".html")
		return nombre + ".html [ok]"
	except Exception as e:
		print("[ERROR/HTMLDESCARGA] Houston, tenemos problemas! (",e,")",sep="")
		return False

def DescargarSitio(sitio,nombre):
	try:
		urlab = urlopen(sitio).url 					### En toda esta sección,se obtiene la ruta donde
		realp = realpath(__file__)					### el script se está ejecutando, y nos movemos a ese directorio
		realp = realp.replace(basename(realp),"")	###
		chdir(realp)								###### MOVERSE AL DIRECTORIO DONDE SE ESTÉ EJECUTANDO EL SCRIPT
		uri = URI_real(sitio)				#-> obtener sólo el URI del sitio
		novositio = sitio.replace(uri,"")	#Obtener la estructura de carpetas (sin el http:// y sin el nombre de archivo)
		try:	makedirs(novositio)			#-> tratar de crear la estructura, de otra manera, crear subdirectorios
		except:	pass
		chdir(novositio)					#-> movernos a dicha carpeta
		urlretrieve(urlab,nombre+".html")	#-> descargar el sitio
		chdir(realp)						#-> movernos de vuelta al directorio del script para no causar problemas
		return nombre + ".html [ok]"
	except Exception as e:
		print("[ERROR/htmlDESCARGA] Houston, tenemos problemas! (",e,")",sep="")	#oopsie woopsie
		return False

def DescargarArchivo(sitio):
	try:
		urlab = urlopen(sitio).url 					### En toda esta sección,se obtiene la ruta donde
		realp = realpath(__file__)					### el script se está ejecutando, y nos movemos a ese directorio
		realp = realp.replace(basename(realp),"")	###
		chdir(realp)								###### MOVERSE AL DIRECTORIO DONDE SE ESTÉ EJECUTANDO EL SCRIPT
		uri = URI_real(sitio)				#-> obtener sólo el URI del sitio
		nomArch = basename(urlab)			#-> obtener el nombre del archivo que se va descargar
		novositio = sitio.replace(uri,"").replace(nomArch,"")	#Obtener la estructura de carpetas (sin el http:// y sin el nombre de archivo)
		try:	makedirs(novositio)			#-> tratar de crear la estructura, de otra manera, crear subdirectorios
		except:	pass
		chdir(novositio)					#-> movernos a dicha carpeta
		urlretrieve(urlab,nomArch)			#-> descargar el archivo
		chdir(realp)						#-> movernos de vuelta al directorio del script para no causar problemas
		return nomArch+" [OK]"				#ok, todo bien
	except Exception as e:
		print("[ERROR/DESCARGA] Houston, tenemos problemas! (",e,")",sep="")	#oopsie woopsie
		return False

def ValidarURL(sitio):	#Validar que la url empiece con http(s) y que sea válida también
	regex = re.compile(
		r'^(?:http|ftp)s?://' # http:// o https://
		r'(?:(?:[A-Z0-9](?:[A-Z0-9-]{0,61}[A-Z0-9])?\.)+(?:[A-Z]{2,6}\.?|[A-Z0-9-]{2,}\.?)|' #dominios
		r'localhost|' #localhost...
		r'\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3})' # ...ip
		r'(?::\d+)?' # optional port
		r'(?:/?|[/?]\S+)$', re.IGNORECASE)
	if(re.match(regex, sitio)):	return True	#Si la url es válida, regresar True
	else:	return False	#En otro caso, False

def AbrirSitio(sitio):
	if(sitio.startswith("ftp://")):
		print("[ERROR] FTP no soportado. Saliendo")
		return None
	try:	website = urlopen(sitio)	#Tratar de abrir el sitio
	except Exception as e:
		print("[ERROR/APERTURA] Houston, tenemos problemas! (",e,")",sep="")
		return None
	try:
		return website.read().decode(CodificacionSitio(sitio))	#Leer el sitio y decodificarlo acorde a su codificación
	except TypeError as e:	print("[W/apertura] No hay nada para decodificar (",sitio,")",sep="")

def URI_real(sitio):	return "{0.scheme}://{0.netloc}/".format(urlsplit(sitio))#Esta función se encarga de sacar el URI real, por ejemplo
#http://aaa.com/asda/asdasd --> aaa.com
def fixEnlaces(listaSitios):	#Arregla enlaces que empiezan con //, o aquellos
	nuevo = []					#que sólo son enlaces a algo vacío (#,https://,http://)
	for item in  listaSitios:	
		if(item.startswith("//")):	nuevo.append(item.replace('//',''))
		elif(item in ["#","https://","ftp://","http://","/"] or item.startswith("{{")):	pass
		else: nuevo.append(item)
	return nuevo

def agregarPrefijo(listaSitios):	#Esta función agrega el "https://" a algunos sitios que carecen de esto, como es el caso de por ejemplo abc.com
	nuevo = []
	for item in listaSitios:
		if(item.startswith("http")):	nuevo.append(item)	#Agregar tal cual si el sitio empieza con http (incluye https también)
		elif(item.startswith("data:")):	pass 				#Si el URL es un archivo de datos (blob/b64) omitir, pues son cosas dinámicas
		else:	nuevo.append("https://"+item)				#Agregarle el "https://" en caso de que no sean los casos anterioresl
	return nuevo

def fixSubenlace(listaSitios,uri):	return	[(uri+item[1:]) if(item.startswith("/")) else item for item in listaSitios]
#^Esta función elimina el "/" que algunos sitios utilizan para recursos y les pega el URI al principio para que funcionen correctamente al descargar.
def SitioPadreSolo(listaSitios,uri):	return [item for item in listaSitios if(item.startswith(uri))]
#^Esta función regresa sólo aquellos enlaces que formen parte del "sitio padre", es decir, no incluye dominios que sean utilizados como CDNs o servidores de anuncios.
def NombrePagina(sopa):	return sopa.title.string.strip()	#Título de la página

def otroFix(listaSitios,uri):	#Este fix se encarga de arreglar aquellos enlaces
	nuevo = []					#que sólo son, por ejemplo 'documentos/', es decir, que no empiezan con "/" 
	for item in listaSitios:
		if(ValidarURL(item)):	nuevo.append(item)	#<--- si es URL, agregar tal cual
		else:	
			if(uri.endswith("/") or item.startswith("/")):	nuevo.append(uri+item)	#<--- en caso de que el URI tenga al final un / o que el ítem lo tenga al principio
			else:	nuevo.append(uri+"/"+item)										#En caso de que no lo tenga
	return nuevo

def manageURL(arregloLink, base):
	newArreglo = []
	print(arregloLink)
	for link in arregloLink:
		if(len(link)>len(base)):
			if('?' not in link):
				newArreglo.append(link)
	for link in newArreglo:
		processThread = threading.Thread(target=wpyget, args=(link,))  # <- note extra ','
		processThread.start()
		processThread.join()

def wpyget(sitio):
	if(ValidarURL(sitio)):			#Realizar una validación al principio
		html = AbrirSitio(sitio)	#Código html (texto plano)
		if(html is None):			#Cuando no hay cuerpo de html, se trata de un archivo/recurso
			print("[DESCARGANDO]",DescargarArchivo(sitio))
			sys.exit(0)	#En caso de que haya algún error
		uri = URI_real(sitio)			#Obtener URL real
		try:
			sopa = BeautifulSoup(html,"lxml")	#Convertir el html a objeto de BeautifulSoup para poder leer
		except Exception as e:	print("Error al leer la página! Use lxml en la línea 55: ",e)
		finally:	
			if(sopa):	pass 	#En caso de que se haya podido abrir, no hacer nada
			else:				#Si no se pudo abrir, intentar abrir con cualquier motor disponible en el sistema
				sopa = BeautifulSoup(html)
		tituloPagina = re.sub('\W+','', NombrePagina(sopa))	#Eliminar símbolos 'raros' de una cadena
		print("Título de la página:",tituloPagina.replace(" ",""))	#imprimir el título de la página
		try:	mkdir(tituloPagina)
		except:	pass
		chdir(tituloPagina)
		print("[HTML/DESCARGANDO]",DescargarSitio(sitio,tituloPagina))
		links = [a.get('href') for a in sopa.find_all('a', href=True)]	#Sacar todos los enlaces (href)  <--- Estos ENLACES se sacaron con BeautifulSoup
		alinks = [a for a in sopa.find_all(src=True)]	#Sacar todos los recursos (href)	<--- Estos RECURSOS se sacaron con BeautifulSoup
		recursos =  SitioPadreSolo(fixSubenlace(fixEnlaces(re.findall('src="([^"]+)"',str(html))),uri),uri) #<--- Estos RECURSOS se sacaron con exp.regulares
		nuevosRecursos = [str(i) for i in alinks]	#Convertir a cadena todos los recursos que se sacaron con BeautifulSoup
		recursos2 = re.findall('src="([^"]+)"',str(nuevosRecursos))		#Sacar todos los enlaces que se encontraron con BeautifulSoup 
		linksexternos =  SitioPadreSolo(otroFix(fixSubenlace(fixEnlaces(links),uri),sitio),uri)	#Quitar basura que pueda estar en el html, y arreglar subenlaces |EL BUENO DE LOS ENLACES
		recursos = agregarPrefijo(recursos)
		recursos2 = SitioPadreSolo( fixSubenlace( fixEnlaces(recursos2),uri),uri)  
		recursosChidos = set(recursos + recursos2)			###<--- ESTE ES EL BUENO (DE LOS RECURSOS)
		nuevosURL = []
		arrayImgFiles =['tif','tiff','bmp','jpg','jpeg','gif','png']
		for recurso in recursosChidos:
			print("[SRC/DESCARGANDO]",recurso,DescargarArchivo(recurso))	#Estos son los recursos presentes en la página (aquellos que apuntan a SRC)
		for i in linksexternos:
			if("application" in TipoContenido(i) or any(ext in i for ext in arrayImgFiles) ):		#Si el tipo de contenido empieza con "application", se trata de algún archivo
				print("[LINK/DESCARGANDO]",i,DescargarArchivo(i))		#Aquí, se descargan esos archivos
			else: 	
				print("[LINK]",i)										#Estos son los enlaces que tenemos que seguir visitando
				nuevosURL.append(i)	
		print("URI",uri)
		manageURL(nuevosURL,sitio)

if __name__ == "__main__":
	try:
		executor = ThreadPoolExecutor(max_workers=2)
		pagina = "http://blogs.fad.unam.mx/academicos/luis_serrano/wp-content/uploads/2011/"
		wpyget(pagina)
	except:	pass