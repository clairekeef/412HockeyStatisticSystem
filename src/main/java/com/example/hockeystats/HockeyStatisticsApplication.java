package com.example.hockeystats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HockeyStatisticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(HockeyStatisticsApplication.class, args);
    }

}

// go to http://localhost:8080/ to view

/*
mvn clean compile
mvn spring-boot:run
 */

// 1
/*
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
 */

// 2
/*
eval "$(/opt/homebrew/bin/brew shellenv)"

then paste 
brew -v
and if you see a version than its working
 */

//3
/*
brew install maven

mvn -v
^ to check version once installed
 */

//4
/*
mvn clean install
 */