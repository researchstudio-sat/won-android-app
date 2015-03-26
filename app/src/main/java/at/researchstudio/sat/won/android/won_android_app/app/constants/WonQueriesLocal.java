package at.researchstudio.sat.won.android.won_android_app.app.constants;

/**
 * Created by fsuda on 05.03.2015.
 */
public class WonQueriesLocal {
    public static final String SPARQL_PREFIX = "PREFIX rdfs:  <http://www.w3.org/2000/01/rdf-schema#>"+
            "PREFIX geo:   <http://www.w3.org/2003/01/geo/wgs84_pos#>"+
            "PREFIX xsd:   <http://www.w3.org/2001/XMLSchema#>"+
            "PREFIX rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
            "PREFIX won:   <http://purl.org/webofneeds/model#>"+
            "PREFIX gr:    <http://purl.org/goodrelations/v1#>"+
            "PREFIX sioc:  <http://rdfs.org/sioc/ns#>"+
            "PREFIX ldp:   <http://www.w3.org/ns/ldp#>"+
            "PREFIX dc:    <http://purl.org/dc/elements/1.1/>"+
            "PREFIX msg:   <http://purl.org/webofneeds/message#>";

    public static final String SPARQL_ALL_TRIPLES = SPARQL_PREFIX + "SELECT * WHERE { graph ?g {?s ?p ?o} . }";

    public static final String SPARQL_ALL_NEEDS = SPARQL_PREFIX + "SELECT * WHERE " +
            "{ ?need won:hasContent ?x; " +
            "won:isInState ?state; " +
            "won:hasBasicNeedType ?type." +
            "?x   won:hasTextDescription ?desc; " +
            "won:hasTag ?tag; " +
            "dc:title ?title. " +
            "?x won:hasContentDescription ?y. " +
            "OPTIONAL {" +
            "?y won:hasLocationSpecification ?loc. " +
            "?loc won:hasAddress ?address; " +
            "geo:latitude ?lat; " +
            "geo:longitude ?lng." +
            "} " +
            "OPTIONAL {" +
            "?y won:hasTimespecification ?time. " +
            "?time won:hasEndTime ?endtime; " +
            "won:hasStartTime ?starttime; " +
            "won:hasRecurInfiniteTimes ?recinf; " +
            "won:hasRecursIn ?recin." +
            "}" +
            "}";

    public static final String SPARQL_ALL_NEEDS_FILTERED_BY_URI_PLUS_COUNT = SPARQL_PREFIX + "SELECT ?state ?type ?desc ?tag ?lat ?lng ?address ?endtime ?starttime ?recinf ?recin ?need ?title ?connState (count(?connState) as ?connCount) WHERE { ?need won:hasContent ?x; won:isInState ?state; won:hasBasicNeedType ?type. ?need won:hasConnections ?connections. ?connections rdfs:member ?connection. ?connection won:hasRemoteNeed ?remoteNeed; won:hasConnectionState ?connState. ?x  won:hasTextDescription ?desc; won:hasTag ?tag; dc:title ?title. ?x won:hasContentDescription ?y. OPTIONAL {?y won:hasLocationSpecification ?loc. ?loc won:hasAddress ?address; geo:latitude ?lat; geo:longitude ?lng.} OPTIONAL {?y won:hasTimespecification ?time. ?time won:hasEndTime ?endtime; won:hasStartTime ?starttime; won:hasRecurInfiniteTimes ?recinf; won:hasRecursIn ?recin.}FILTER (?need in (::need::))} GROUP BY ?state ?type ?desc ?tag ?lat ?lng ?address ?endtime ?starttime ?recinf ?recin ?need ?title ?connState";

    public static final String SPARQL_NEEDS_FILTERED_BY_URI = SPARQL_PREFIX + "SELECT * WHERE " +
            "{ ?need won:hasContent ?x; " +
            "won:isInState ?state; " +
            "won:hasBasicNeedType ?type." +
            "?x  won:hasTextDescription ?desc; " +
            "won:hasTag ?tag; " +
            "dc:title ?title. " +
            "?x won:hasContentDescription ?y. " +
            "OPTIONAL {" +
            "?y won:hasLocationSpecification ?loc. " +
            "?loc won:hasAddress ?address; " +
            "geo:latitude ?lat; " +
            "geo:longitude ?lng." +
            "} " +
            "OPTIONAL {" +
            "?y won:hasTimespecification ?time. " +
            "?time won:hasEndTime ?endtime; " +
            "won:hasStartTime ?starttime; " +
            "won:hasRecurInfiniteTimes ?recinf; " +
            "won:hasRecursIn ?recin." +
            "}" +
            "FILTER (?need in (::need::))" +
            "}";

    public static final String SPARQL_CONNECTIONS_FILTERED_BY_NEED_URI = SPARQL_PREFIX + "SELECT * WHERE " +
            "{ " +
            "?need won:hasConnections ?connections. " +
            "?connections rdfs:member ?connection. " +
            "?connection won:hasConnectionState ?state; " +
            "won:hasRemoteNeed ?remoteNeed; " +
            "won:belongsToNeed ?localNeed. " +
            "FILTER (?need in (::need::))"+
            "}";

    public static final String SPARQL_CONNECTION_FILTERED_BY_CONNECTION_URI = SPARQL_PREFIX + "SELECT * WHERE " +
            "{ " +
            "?need won:hasConnections ?connections. " +
            "?connections rdfs:member ?connection. " +
            "?connection won:hasConnectionState ?state; " +
            "won:hasRemoteNeed ?remoteNeed; " +
            "won:belongsToNeed ?localNeed. " +
            "FILTER (?connection in (::connection::))"+
            "}";

    public static final String SPARQL_CONNECTIONS_FILTERED_BY_NEED_URI_AND_CONNECTION_STATE = SPARQL_PREFIX + "SELECT * WHERE {?need won:hasConnections ?connections. ?connections rdfs:member ?connection. ?connection won:hasConnectionState ?state; won:hasRemoteNeed ?remoteNeed; won:belongsToNeed ?localNeed. FILTER ((?need in (::need::)) && ?state in (::state::))}";

    /*public static final String SPARQL_MESSAGES_BY_CONNECTION = SPARQL_PREFIX + "SELECT ?message "+
            "WHERE {"+
            "?connection won:hasEventContainer ?event ."+
            "?event won:hasTextMessage ?message ."+
            "}";*/

    public static final String SPARQL_NEED2 = SPARQL_PREFIX + "SELECT * WHERE"+
            "{"+
            "?connection won:hasEventContainer ?container. "+
            "?container rdfs:member ?event. "+
            "?event won:hasTextMessage ?text."+
            "}";


    public static final String SPARQL_EVENTS = SPARQL_PREFIX + "SELECT * WHERE {?need won:hasConnections ?connections. ?connections rdfs:member ?connection. ?connection won:hasEventContainer ?events. ?events rdfs:member ?event. ?event msg:hasMessageType ?msgType. OPTIONAL {?event won:hasTextMessage ?msgText.} FILTER (?need IN (::need::))}";
    public static final String SPARQL_EVENTS_BY_CONNECTION_URI = SPARQL_PREFIX + "SELECT * WHERE {?need won:hasConnections ?connections. ?connections rdfs:member ?connection. ?connection won:hasEventContainer ?events. ?events rdfs:member ?event. ?event msg:hasMessageType ?msgType. OPTIONAL {?event won:hasTextMessage ?msgText.} FILTER (?connection IN (::connection::))}";


    //public static final String SPARQL_MY_NEED = "SELECT * WHERE {?need won:containedInPrivateGraph ?graph. ?need won:hasContent ?x; won:isInState ?state. ?x won:hasTextDescription ?desc; won:hasTag ?tag; dc:title ?title.}";
    //public static final String SPARQL_NEEDS_FILTERED_BY_UUIDS = "SELECT * WHERE { ?need won:hasContent ?x; won:isInState ?state. ?x won:hasTextDescription ?desc; won:hasTag ?tag; dc:title ?title. filter (?need in (<http://rsa021.researchstudio.at:8080/won/resource/need/5630666034445812000>))}";

    //queryString = WonQueriesLocal.SPARQL_PREFIX +
//                    "SELECT ?need ?connection ?need2 WHERE {" +
//                    //"graph ?g1 { ?need won:hasConnections ?connections .}" +
//                    "?need won:hasConnections ?connections ." +
//                    //"graph ?g2 { ?connections rdfs:member ?connection .}" +
//                    "?connections rdfs:member ?connection ." +
//                    //"graph ?g3 {?connection won:hasRemoteNeed ?need2.}"+
//                    "?connection won:hasRemoteNeed ?need2."+
//                    "}";
}

