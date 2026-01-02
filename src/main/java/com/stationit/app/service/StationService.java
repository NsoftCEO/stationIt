package com.stationit.app.service;

import java.util.HashMap; // 추가
import java.util.List;
import java.util.Map; // 추가

import org.springframework.stereotype.Service;

import com.stationit.app.dto.Station;

@Service
public class StationService {

    // 실제 노선 순서와 노선명을 포함하도록 샘플 데이터를 수정합니다. (2호선 일부)
	private static final List<Station> stationDatabase = List.of(
            new Station("교대역", 37.4934, 127.0142, "2호선", 19), // (221) - 이전 역으로 추가
            new Station("강남역", 37.4979, 127.0276, "2호선", 20), // 2호선 (222)
            new Station("역삼역", 37.5006, 127.0364, "2호선", 21), // (223)
            new Station("선릉역", 37.5044, 127.0489, "2호선", 22), // (224)
            new Station("삼성역", 37.5088, 127.0631, "2호선", 23), // (225)
            new Station("종합운동장역", 37.5111, 127.0736, "2호선", 24) // (226)
            // new Station("홍대입구역", 37.5569, 126.9239, "2호선", 30),
            // new Station("시청역", 37.5658, 126.9751, "2호선", 0)
    );

    /**
     * 현재 위치(위도, 경도)를 기준으로 가장 가까운 지하철역을 찾습니다.
     * @param userLat 사용자의 현재 위도
     * @param userLon 사용자의 현재 경도
     * @return 가장 가까운 Station 객체
     */
    public Station findNearestStation(double userLat, double userLon) {
        Station nearestStation = null;
        double minDistance = Double.MAX_VALUE;

        for (Station station : stationDatabase) {
            double distance = calculateDistance(userLat, userLon, station.latitude(), station.longitude());

            if (distance < minDistance) {
                minDistance = distance;
                nearestStation = station;
            }
        }
        return nearestStation;
    }

    /**
     * 가장 가까운 역 및 이전/다음 역 정보를 반환합니다.
     * @param userLat 사용자의 현재 위도
     * @param userLon 사용자의 현재 경도
     * @return "nearest", "previous", "next" 키를 가진 Map
     */
    public Map<String, Station> getNearestStationInfo(double userLat, double userLon) {
        Map<String, Station> result = new HashMap<>();

        // 1. 가장 가까운 역 찾기
        Station nearestStation = findNearestStation(userLat, userLon);
        if (nearestStation == null) {
            return result; // 빈 맵 반환
        }
        result.put("nearest", nearestStation);

        // 2. 이전 역 찾기 (같은 노선, 순서 - 1)
        Station previousStation = stationDatabase.stream()
                .filter(s -> s.lineName().equals(nearestStation.lineName()) &&
                             s.stationOrder() == nearestStation.stationOrder() - 1)
                .findFirst()
                .orElse(null);
        
        if (previousStation != null) {
            result.put("previous", previousStation);
        }

        // 3. 다음 역 찾기 (같은 노선, 순서 + 1)
        Station nextStation = stationDatabase.stream()
                .filter(s -> s.lineName().equals(nearestStation.lineName()) &&
                             s.stationOrder() == nearestStation.stationOrder() + 1)
                .findFirst()
                .orElse(null);

        if (nextStation != null) {
            result.put("next", nextStation);
        }
        
        // test
        result.put("2", new Station("신림", userLon, userLon, "2호선", 0));
        result.put("3", new Station("봉천", userLon, userLon, "2호선, 3호선", 0));

        return result;
    }

    /**
     * 두 지점 간의 거리(m)를 계산하는 Haversine 공식
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구의 반지름 (단위: km)

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c * 1000; // km를 m로 변환
    }
}