package com.cisbox.quarkus.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import com.cisbox.quarkus.entity.Game;
import com.cisbox.quarkus.entity.Season;
import com.cisbox.quarkus.entity.User;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import org.eclipse.microprofile.config.ConfigProvider;

import io.quarkus.cache.CacheInvalidateAll;
import io.quarkus.cache.CacheResult;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Getter;

@ApplicationScoped
@RegisterForReflection
@Named("EntityPersister")
public class CsvEntityPersister implements EntityPersister{   
    @Getter private String userFilePath = null;
    @Getter private String seasonFilePath = null;
    @Getter private String gameFilePath = null;

    private CsvEntityPersister(){
        System.out.println(new File(".").getAbsolutePath());
        userFilePath = ConfigProvider.getConfig().getValue("scoreboard.data.directory", String.class) + "/user.csv";
        seasonFilePath = ConfigProvider.getConfig().getValue("scoreboard.data.directory", String.class) + "/season.csv";
        gameFilePath = ConfigProvider.getConfig().getValue("scoreboard.data.directory", String.class) + "/game.csv";
    }

    @CacheResult(cacheName = "user-cache")
    public List<User> readUsers(){
        try {
            return new CsvToBeanBuilder<User>(new FileReader(userFilePath)).withType(User.class).build().parse();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @CacheInvalidateAll(cacheName = "user-cache")
    public int writeUsers(Collection<User> userList){
        try {
            Writer writer = new FileWriter(userFilePath);
            StatefulBeanToCsv<User> beanToCsv = new StatefulBeanToCsvBuilder<User>(writer).build();
            beanToCsv.write(userList.iterator());        
            writer.close();
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            return 1;
        }
        return 0;
    }

    @CacheResult(cacheName = "season-cache")
    public List<Season> readSeasons(){
        try {
                return new CsvToBeanBuilder<Season>(new FileReader(seasonFilePath)).withType(Season.class).build().parse();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @CacheInvalidateAll(cacheName = "season-cache")
    public int writeSeasons(List<Season> seasonList){
        try {
            
            Writer writer = new FileWriter(seasonFilePath);
            StatefulBeanToCsv<Season> beanToCsv = new StatefulBeanToCsvBuilder<Season>(writer).build();
            beanToCsv.write(seasonList);        
            writer.close();
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            return 1;
        }
        return 0;
    }

    @CacheResult(cacheName = "game-cache")
    public List<Game> readGames(){
        try {
            return new CsvToBeanBuilder<Game>(new FileReader(gameFilePath)).withType(Game.class).build().parse();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @CacheInvalidateAll(cacheName = "game-cache")
    public int writeGames(List<Game> gameList){
        try {
            Writer writer = new FileWriter(gameFilePath);
            StatefulBeanToCsv<Game> beanToCsv = new StatefulBeanToCsvBuilder<Game>(writer).build();
            beanToCsv.write(gameList);        
            writer.close();
        } catch (IOException | CsvDataTypeMismatchException | CsvRequiredFieldEmptyException e) {
            return 1;
        }
        return 0;
    }
}
