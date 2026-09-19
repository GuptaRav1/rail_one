package com.example.railone.data

import java.util.PriorityQueue

object RailwayGraph {
    private val adjacencyList = mutableMapOf<String, MutableList<Pair<String, Double>>>()

    init {
        // Western Line
        val western = "CHURCHGATE,1.4,MARINE LINES,1.3,CHARNI ROAD,1.5,GRANT ROAD,1.1,MUMBAI CENTRAL,1.5,MAHALAXMI,1.4,LOWER PAREL,1.2,PRABHADEVI,1.5,DADAR,1.4,MATUNGA ROAD,1.5,MAHIM,1.8,BANDRA,1.5,KHAR ROAD,1.5,SANTACRUZ,1.5,VILE PARLE,2.2,ANDHERI,2.0,JOGESHWARI,1.5,RAM MANDIR,1.5,GOREGAON,2.5,MALAD,1.8,KANDIVALI,2.5,BORIVALI,2.5,DAHISAR,3.5,MIRA ROAD,3.5,BHAYANDAR,4.5,NAIGAON,4.0,VASAI ROAD,4.0,NALLASOPARA,4.5,VIRAR,8.0,VAITARNA,7.0,SAPHALE,6.0,KELVE ROAD,8.0,PALGHAR,7.0,UMROLI,6.0,BOISAR,9.0,VANGAON,12.0,DAHANU ROAD"
        parseLine(western)

        // Central Line
        val central = "CSMT,1.3,MASJID,1.1,SANDHURST ROAD,1.7,BYCULLA,1.1,CHINCHPOKLI,1.2,CURREY ROAD,1.1,PAREL,1.2,DADAR,1.5,MATUNGA,2.5,SION,2.5,KURLA,2.0,VIDYAVIHAR,2.0,GHATKOPAR,3.5,VIKHROLI,1.8,KANJURMARG,1.8,BHANDUP,2.0,NAHUR,2.5,MULUND,2.5,THANE,2.5,KALWA,3.5,MUMBRA,2.5,DIVA,4.0,KOPAR,1.5,DOMBIVLI,1.5,THAKURLI,3.0,KALYAN"
        parseLine(central)

        // Central Line Kasara Branch
        parseLine("KALYAN,4.0,SHAHAD,3.0,AMBIVLI,5.0,TITWALA,7.0,KHADAVLI,7.0,VASIND,6.0,ASANGAON,9.0,ATGAON,12.0,THANSIT,5.0,KHARDI,7.0,UMBERMALI,10.0,KASARA")
        
        // Central Line Khopoli Branch
        parseLine("KALYAN,3.0,VITHALWADI,2.0,ULHASNAGAR,2.0,AMBERNATH,8.0,BADLAPUR,9.0,VANGANI,4.0,SHELU,4.0,NERAL,7.0,BHIVPURI ROAD,7.0,KARJAT,3.0,PALASDARI,6.0,KELAVLI,2.0,DOLAVLI,3.0,LOWJEE,3.0,KHOPOLI")

        // Harbour Line
        val harbour = "CSMT,1.3,MASJID,1.1,SANDHURST ROAD,1.1,DOCKYARD ROAD,1.2,REAY ROAD,1.3,COTTON GREEN,1.5,SEWRI,2.0,VADALA ROAD,2.5,GURU TEGH BAHADUR NAGAR,1.5,CHUNABHATTI,2.5,KURLA,1.5,TILAK NAGAR,1.5,CHEMBUR,1.5,GOVANDI,2.5,MANKHURD,7.5,VASHI,1.5,SANPADA,1.5,JUINAGAR,2.0,NERUL,1.5,SEAWOODS-DARAVE,2.5,CBD BELAPUR,2.5,KHARGHAR,2.5,MANSAROVAR,2.5,KHANDESHWAR,3.5,PANVEL"
        parseLine(harbour)

        // Harbour Line - Western Branch
        parseLine("VADALA ROAD,2.0,KING'S CIRCLE,1.5,MAHIM")

        // Trans-Harbour Line
        val transHarbour = "THANE,3.0,DIGHA GAON,2.0,AIROLI,2.5,RABALE,1.5,GHANSOLI,2.0,KOPAR KHAIRANE,2.0,TURBHE,2.0,SANPADA"
        parseLine(transHarbour)
        addRoute("TURBHE", "JUINAGAR", 2.0)

        // Uran Line
        val uran = "NERUL,1.5,SEAWOODS-DARAVE,2.5,SAGAR SANGAM,2.0,TARGHAR,2.0,BAMANDONGRI,2.0,KHARKOPAR,2.5,GAVHAN,2.5,RANJANPADA,2.5,NHAVA SHEVA,2.5,DRONAGIRI,2.0,URAN"
        parseLine(uran)
        addRoute("CBD BELAPUR", "SAGAR SANGAM", 2.5)

        // Additional Interchanges (Foot-over bridges connecting stations on different lines)
        addRoute("PRABHADEVI", "PAREL", 0.5)
    }

    private fun addRoute(s1: String, s2: String, dist: Double) {
        adjacencyList.computeIfAbsent(s1) { mutableListOf() }.add(Pair(s2, dist))
        adjacencyList.computeIfAbsent(s2) { mutableListOf() }.add(Pair(s1, dist))
    }

    private fun parseLine(line: String) {
        val parts = line.split(",")
        for (i in 0 until parts.size - 2 step 2) {
            addRoute(parts[i], parts[i + 2], parts[i + 1].toDouble())
        }
    }

    fun getAllStations(): List<String> = adjacencyList.keys.sorted()

    data class RouteResult(val distanceKm: Int, val path: List<String>, val fare: Int, val via: String)

    fun findShortestPath(source: String, dest: String): RouteResult? {
        if (!adjacencyList.containsKey(source) || !adjacencyList.containsKey(dest)) return null
        if (source == dest) return RouteResult(0, listOf(source), 5, "------")

        val distances = mutableMapOf<String, Double>().withDefault { Double.MAX_VALUE }
        val previous = mutableMapOf<String, String>()
        val pq = PriorityQueue<Pair<String, Double>>(compareBy { it.second })

        distances[source] = 0.0
        pq.add(Pair(source, 0.0))
        val visited = mutableSetOf<String>()

        while (pq.isNotEmpty()) {
            val element = pq.poll() ?: break
            val current = element.first
            val currentDist = element.second
            
            if (current == dest) break
            if (visited.contains(current)) continue
            visited.add(current)

            adjacencyList[current]?.forEach { (neighbor, weight) ->
                if (!visited.contains(neighbor)) {
                    val newDist = currentDist + weight
                    if (newDist < distances.getValue(neighbor)) {
                        distances[neighbor] = newDist
                        previous[neighbor] = current
                        pq.add(Pair(neighbor, newDist))
                    }
                }
            }
        }

        if (!previous.containsKey(dest)) return null

        val path = mutableListOf<String>()
        var curr: String? = dest
        while (curr != null) {
            path.add(curr)
            curr = previous[curr]
        }
        path.reverse()

        val totalDistance = Math.ceil(distances.getValue(dest)).toInt()
        val fare = calculateFare(totalDistance)
        val via = calculateVia(path)

        return RouteResult(totalDistance, path, fare, via)
    }

    private fun calculateFare(distanceKm: Int): Int {
        return when {
            distanceKm <= 10 -> 5
            distanceKm <= 25 -> 10
            distanceKm <= 45 -> 15
            distanceKm <= 70 -> 20
            distanceKm <= 90 -> 25
            distanceKm <= 110 -> 30
            else -> 35
        }
    }

    private fun calculateVia(path: List<String>): String {
        if (path.size <= 2) return "------"
        val interchanges = setOf(
            "DADAR", "KURLA", "VADALA ROAD", "THANE", "SANDHURST ROAD", 
            "MAHIM", "BANDRA", "NERUL", "SANPADA", "JUINAGAR", "KOPAR", "PAREL"
        )
        val usedInterchanges = path.subList(1, path.size - 1).filter { interchanges.contains(it) }
        
        if (usedInterchanges.isEmpty()) return "------"
        
        val abbr = mapOf(
            "DADAR" to "DDR", "KURLA" to "CLA", "VADALA ROAD" to "VDLR", 
            "THANE" to "TNA", "SANDHURST ROAD" to "SNRD", "MAHIM" to "MM", 
            "BANDRA" to "BA", "NERUL" to "NEU", "SANPADA" to "SNPD", 
            "JUINAGAR" to "JNJ", "KOPAR" to "KOPR", "PAREL" to "PR"
        )
        
        val viaText = usedInterchanges.take(2).joinToString("-") { abbr[it] ?: it.take(3) }
        return "1RT>>$viaText"
    }
}