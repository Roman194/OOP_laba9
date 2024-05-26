package com.example.oop_laba9.controller;

import com.example.oop_laba9.models.Song;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.json.JSONArray;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@WebServlet("/songs_table")
public class SongsController extends HttpServlet {

    @Override
    protected  void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

        RequestDispatcher requestDispatcher = req.getRequestDispatcher("view/songs.jsp");
        requestDispatcher.forward(req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

        resp.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("UTF-8");

        Random rand = new Random();
        String randValue = Integer.toString(rand.nextInt(1000));

        Song song = new Song( //getting new song object from view
                randValue,
                req.getParameter("song_name"),
                req.getParameter("author"),
                req.getParameter("album"),
                req.getParameter("length"),
                req.getParameter("year")
        );

        String path = "songs.json";
        ServletContext context = getServletContext();
        InputStream inputStream = context.getResourceAsStream(path);

        if (inputStream != null) {

            //getting songs json from file
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder jsonString = new StringBuilder();

            while (true) {
                String text = bufferedReader.readLine();
                if (text == null)
                    break;
                jsonString.append(text);
            }

            //converting songs json to POJO array
            Type listType = new TypeToken<ArrayList<Song>>(){}.getType();
            Gson gson = new Gson();
            List<Song> songs = gson.fromJson(jsonString.toString(), listType);

            //adding new song object to array
            songs.add(song);

            //converting POJO array to json string
            String outputJsonString = gson.toJson(songs);
            JSONArray array = new JSONArray(outputJsonString);

            //writing json string to source json file
            String fullPath;
            try {
                fullPath = new File(SongsController.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getParentFile().getParentFile().getParent();
                fullPath += File.separator + "src" + File.separator + "main" + File.separator + "webapp" + File.separator + "songs.json";
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }

            try(FileWriter writer = new FileWriter(fullPath)){
                writer.write(array.toString(4));
                writer.flush();
                writer.close();
            } catch (IOException ex){
                System.out.println("full Error!!!");
                ex.printStackTrace();
            }

        }

        doGet(req, resp);
    }
}
