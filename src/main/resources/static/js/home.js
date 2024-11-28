//Se sobrecarga el botón toggle
$(function() {
  // Sidebar toggle behavior
  $('#sidebarCollapse').on('click', function() {
    $('#sidebar, #content').toggleClass('active');
  });

});

//Se sobrecarga botón de Guardar de ventana modal de Nueva Película y 
//evento onclick de Listar Películas
$(document).ready(function(){
  
/////////////////////////////////////////////////////////////////////////////  
//Aquí deberías implementar la llamada asincrona al servidor para enviar por
//método POST los datos de la nueva película.  
  $('body').on('click', '#GuardarPelicula', function(){
    //En esta zona debería ubicarse la llamada asíncrona al servidor enviando los datos de la nueva película






    $("#NuevaPeliculaCenter").modal('hide'); //Se oculta la ventana modal
    $('body').removeClass('modal-open'); //Eliminamos la clase del body para poder hacer scroll
    $('.modal-backdrop').remove();//eliminamos el backdrop del modal
    
    //Si se puedo guardar una nueva película entonces mostrar la lista de peliculas:
     $('#ListarPeliculasVisual').modal('show');
  })


  ///////////////////////////////////////////////////////////////////////
  //Aquí debería implementar la llamada asíncrona para obtener los datos de las películas
  //y cargarlos dentro de la capa #htmlListaPeliculas
  $('body').on('click', '#IdListarPeliculas', function(){

    //Sustituir este código demo por el que corresponda:
    $("#htmlListaPeliculas").html("Generar aquí la lista de películas");
  })
  
  
})