package com.example.sitahackathon.retrofit

import org.json.JSONObject
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

class Utils {

    companion object{
        fun getEstimatedDepartureFromRequest(response: JSONObject): String {
            val departure = response.getJSONArray("flightRecords").getJSONObject(0)
                    .getJSONObject("departure")
            if (departure.has("estimated")){
                return departure.getString("estimated")
            }
            else{
                return departure.getString("scheduled")
            }

        }
        fun getEstimatedArrivalFromRequest(response: JSONObject): String {
            val arrival = response.getJSONArray("flightRecords").getJSONObject(0)
                .getJSONObject("arrival")
            if (arrival.has("estimated")){
                return arrival.getString("estimated")
            }
            else{
                return arrival.getString("scheduled")
            }

        }

        fun getSumOfLongestCheckpointsOrBufferTimeInMinutes(response: JSONObject):Int{
            if (!response.has("current")){
                return 180 //3 hours in case no details are available about checkpoints in the airport (may change)
            }
            val data = ArrayList<Int>()
            val length = response.getJSONArray("current").length()
            for(i in 0 until length){
                data.add(response.getJSONArray("current").getJSONObject(i).getInt("projectedMaxWaitMinutes"))
            }
            data.sort()
            if (length>=3)
                return data[length-1] + data[length-2] + data[length-3]
            else{
                var sum = 0
                for(i in 0 until length)sum+=data[i]
                return sum
            }

        }

        fun getDepartureAirportCode(response: JSONObject) =
            response.getJSONArray("flightRecords").getJSONObject(0)
                .getJSONObject("departure").getJSONObject("airport").getString("iataCode")


        fun getLatitudeAndLongitude(response: JSONObject): String {
//            if (!response.has("airports")){ errors should be checked via status codes in the callbacks and not here
//                return Pair(0.0,0.0)
//            }
            val airport = response.getJSONArray("airports").getJSONObject(0)
            return "${airport.getDouble("latitude")},${airport.getDouble("longitude")}"
        }


        fun getTimeInMinutesFromGraphHopperResponse(response: JSONObject): Int{ //returns the longest path
            val paths = response.getJSONArray("paths")
            var mx = -1
            for(i in 0 until paths.length()){
                if (paths.getJSONObject(i).getInt("time")>mx){
                    mx = paths.getJSONObject(i).getInt("time")
                }
            }
            return mx/1000/60
        }


        fun convertStringToZonedDateTime(data: String): ZonedDateTime {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")
            return ZonedDateTime.parse(data, formatter)
        }
    }
}