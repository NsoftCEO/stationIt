package com.stationit.app.controller;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stationit.app.dto.Station;
import com.stationit.app.service.StationService;

// "http://localhost:3300" 플러터 포트
@CrossOrigin(origins = {"http://localhost:3000"}) 
@RestController
@RequestMapping("/api")
public class StationController {

    private final StationService stationService;

    // 생성자를 통해 StationService를 주입받음
    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    /**
     * 가장 가까운 역을 찾는 API
     * 예: /api/nearest-station?lat=37.55&lon=126.97
     */
    @GetMapping("/nearest-station")
    public Map<String, Station> getNearestStation(@RequestParam("lat") double latitude, @RequestParam("lon") double longitude){
    	System.out.println("getNearestStation 호출");
    	System.out.println("latitude :"+latitude + " longitude:"+ longitude);
        return stationService.getNearestStationInfo(latitude, longitude);
    }
}