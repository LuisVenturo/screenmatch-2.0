package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.DatosEpisodio;
import com.aluracursos.screenmatch.model.DatosSerie;
import com.aluracursos.screenmatch.model.DatosTemporada;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
        json = consumoAPI.obtenerDatos(URL_BASE+URLEncoder.encode(nombreSerie, "UTF-8")+"&season=1"+"&episode=1"+API_KEY);
        DatosEpisodio episodios = conversor.obtenerDatos(json, DatosEpisodio.class);
        System.out.println(episodios);

        //TEMPORADAS
        List<DatosTemporada> temporadas = new ArrayList<>();
        for (int i = 1; i < datosSerie.totalDeTemporadas(); i++) {
            json = consumoAPI.obtenerDatos(URL_BASE+URLEncoder.encode(nombreSerie,"UTF-8")+"&season="+i+API_KEY);
            DatosTemporada datosTemporada = conversor.obtenerDatos(json, DatosTemporada.class);
            temporadas.add(datosTemporada);
        }
        temporadas.forEach(System.out::println);


        for (int i = 1; i < datosSerie.totalDeTemporadas(); i++) {
            List<DatosEpisodio> episodioTemporadas = temporadas.get(i).episodios();
            for (int j = 0; j < episodioTemporadas.size(); j++) {
                System.out.println(episodioTemporadas.get(j).titulo());
            }
        }

        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

    }
}
