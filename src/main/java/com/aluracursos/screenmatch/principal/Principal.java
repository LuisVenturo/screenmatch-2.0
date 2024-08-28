package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.DatosEpisodio;
import com.aluracursos.screenmatch.model.DatosSerie;
import com.aluracursos.screenmatch.model.DatosTemporada;
import com.aluracursos.screenmatch.model.Episodio;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private final String API_KEY = "&apikey=d5244f96";
    private final String  URL_BASE = "https://www.omdbapi.com/?t=";

    public void muestraElMenu() throws UnsupportedEncodingException {
        System.out.println("Por favor escribe el nombre de la serie que quieres buscar");
        var nombreSerie = teclado.nextLine();

        //SERIE
        var json = consumoAPI.obtenerDatos(URL_BASE+ URLEncoder.encode(nombreSerie,"UTF-8")+API_KEY);
        DatosSerie datosSerie = conversor.obtenerDatos(json, DatosSerie.class);
        System.out.println(datosSerie);

        //EPISODIOS
//        json = consumoAPI.obtenerDatos(URL_BASE+URLEncoder.encode(nombreSerie, "UTF-8")+"&season=1"+"&episode=1"+API_KEY);
//        DatosEpisodio episodios = conversor.obtenerDatos(json, DatosEpisodio.class);
//        System.out.println(episodios);

        //TEMPORADAS
        //Busca los datos de todas las temporadas
        List<DatosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= datosSerie.totalDeTemporadas(); i++) {
            json = consumoAPI.obtenerDatos(URL_BASE+URLEncoder.encode(nombreSerie,"UTF-8")+"&season="+i+API_KEY);
            DatosTemporada datosTemporada = conversor.obtenerDatos(json, DatosTemporada.class);
            temporadas.add(datosTemporada);
        }
        temporadas.forEach(System.out::println);

        //Mostrar solo el título de los episodios para las temporadas
        //funciónes lambda
//        for (int i = 1; i < datosSerie.totalDeTemporadas(); i++) {
//            List<DatosEpisodio> episodioTemporadas = temporadas.get(i).episodios();
//            for (int j = 0; j < episodioTemporadas.size(); j++) {
//                System.out.println(episodioTemporadas.get(j).titulo());
//            }
//        }
        //mejoría usando funciones lambda
//        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        //Convertir todas las informaciones a una lista del tipo DatosEpisodio

        List<DatosEpisodio> datosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());


        //Obtener los tops 5 episodios
//        System.out.println("Top 5 episodios");
//        datosEpisodios.stream()
//                .filter(e -> !e.evaluacion().equalsIgnoreCase("N/A"))
//                .peek(e -> System.out.println("Primer filtro (N/A)"+e))
//                .sorted(Comparator.comparing(DatosEpisodio::evaluacion).reversed())
//                .peek(e -> System.out.println("Segundo ordenación (M>m)"+e))
//                .map(e -> e.titulo().toUpperCase())
//                .peek(e -> System.out.println("Tercer filtro Mayúscula (M>m)" + e))
//                .limit(5)
//                .forEach(System.out::println);

        //Convirtiendo los datos a una lista del tipo episodio
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d)))
                .collect(Collectors.toList());
//        episodios.forEach(System.out::println);

        //Busqueda de episodios a partir de x año
//        System.out.println("Por favor indica el año a partir del cual deseas ver los episodios: ");
//        var fecha = teclado.nextInt();
//        teclado.nextLine();

//        LocalDate fechaBusqueda = LocalDate.of(fecha, 1, 1);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        episodios.stream()
//                .filter(e -> e.getFechaDeLanzamiento() != null && e.getFechaDeLanzamiento().isAfter(fechaBusqueda))
//                .forEach(e -> System.out.println(
//                        "Temporada "+ e.getTemporada() +
//                                " Episodio "+ e.getTitulo() +
//                                "fecha de lanzamiento "+e.getFechaDeLanzamiento().format(dtf)
//                ));

        //Busca episodios por pedazo del titulo
        System.out.println("Por favor escriba el titulo del episodio que desea ver");
        var pedazoTitulo = teclado.nextLine();
        Optional<Episodio> episodioBuscado = episodios.stream()
                .filter(e -> e.getTitulo().toUpperCase().contains(pedazoTitulo.toUpperCase()))
                .findFirst();

        if (episodioBuscado.isPresent()){
            System.out.println(" Episodio encontrado");
            System.out.println("Los datos son: "+episodioBuscado.get());
        }else {
            System.out.println("Episodio no encontrado");
        }


    }
}
