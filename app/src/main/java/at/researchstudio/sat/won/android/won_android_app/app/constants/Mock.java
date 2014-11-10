/*
 * Copyright 2014 Research Studios Austria Forschungsges.m.b.H.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package at.researchstudio.sat.won.android.won_android_app.app.constants;

import at.researchstudio.sat.won.android.won_android_app.app.enums.ConnectionType;
import at.researchstudio.sat.won.android.won_android_app.app.enums.MessageType;
import at.researchstudio.sat.won.android.won_android_app.app.enums.PostType;
import at.researchstudio.sat.won.android.won_android_app.app.enums.RepeatType;
import at.researchstudio.sat.won.android.won_android_app.app.model.Connection;
import at.researchstudio.sat.won.android.won_android_app.app.model.MessageItemModel;
import at.researchstudio.sat.won.android.won_android_app.app.model.Post;
import at.researchstudio.sat.won.android.won_android_app.app.model.RequestListItemModel;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Sole purpose of this class is to mock objects for the interface before it is implemented
 * Created by fsuda on 13.10.2014.
 */
public class Mock {
    public static Map<UUID, Post> myMockPosts = new HashMap<UUID, Post>();
    public static Map<UUID, Post> myMockMatches = new HashMap<UUID, Post>();
    public static Map<UUID, Connection> myMockConversations = new HashMap<UUID, Connection>();

    public static final String[] CHEESES = {
            "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
            "Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale",
            "Aisy Cendre", "Allgauer Emmentaler", "Alverca", "Ambert", "American Cheese",
            "Ami du Chambertin", "Anejo Enchilado", "Anneau du Vic-Bilh", "Anthoriro", "Appenzell",
            "Aragon", "Ardi Gasna", "Ardrahan", "Armenian String", "Aromes au Gene de Marc",
            "Asadero", "Asiago", "Aubisque Pyrenees", "Autun", "Avaxtskyr", "Baby Swiss",
            "Babybel", "Baguette Laonnaise", "Bakers", "Baladi", "Balaton", "Bandal", "Banon",
            "Barry's Bay Cheddar", "Basing", "Basket Cheese", "Bath Cheese", "Bavarian Bergkase",
            "Baylough", "Beaufort", "Beauvoorde", "Beenleigh Blue", "Beer Cheese", "Bel Paese",
            "Bergader", "Bergere Bleue", "Berkswell", "Beyaz Peynir", "Bierkase", "Bishop Kennedy",
            "Blarney", "Bleu d'Auvergne", "Bleu de Gex", "Bleu de Laqueuille",
            "Bleu de Septmoncel", "Bleu Des Causses", "Blue", "Blue Castello", "Blue Rathgore",
            "Blue Vein (Australian)", "Blue Vein Cheeses", "Bocconcini", "Bocconcini (Australian)",
            "Boeren Leidenkaas", "Bonchester", "Bosworth", "Bougon", "Boule Du Roves",
            "Boulette d'Avesnes", "Boursault", "Boursin", "Bouyssou", "Bra", "Braudostur",
            "Breakfast Cheese", "Brebis du Lavort", "Brebis du Lochois", "Brebis du Puyfaucon",
            "Bresse Bleu", "Brick", "Brie", "Brie de Meaux", "Brie de Melun", "Brillat-Savarin",
            "Brin", "Brin d' Amour", "Brin d'Amour", "Brinza (Burduf Brinza)",
            "Briquette de Brebis", "Briquette du Forez", "Broccio", "Broccio Demi-Affine",
            "Brousse du Rove", "Bruder Basil", "Brusselae Kaas (Fromage de Bruxelles)", "Bryndza",
            "Buchette d'Anjou", "Buffalo", "Burgos", "Butte", "Butterkase", "Button (Innes)",
            "Buxton Blue", "Cabecou", "Caboc", "Cabrales", "Cachaille", "Caciocavallo", "Caciotta",
            "Caerphilly", "Cairnsmore", "Calenzana", "Cambazola", "Camembert de Normandie",
            "Canadian Cheddar", "Canestrato", "Cantal", "Caprice des Dieux", "Capricorn Goat",
            "Capriole Banon", "Carre de l'Est", "Casciotta di Urbino", "Cashel Blue", "Castellano",
            "Castelleno", "Castelmagno", "Castelo Branco", "Castigliano", "Cathelain",
            "Celtic Promise", "Cendre d'Olivet", "Cerney", "Chabichou", "Chabichou du Poitou",
            "Chabis de Gatine", "Chaource", "Charolais", "Chaumes", "Cheddar",
            "Cheddar Clothbound", "Cheshire", "Chevres", "Chevrotin des Aravis", "Chontaleno",
            "Civray", "Coeur de Camembert au Calvados", "Coeur de Chevre", "Colby", "Cold Pack",
            "Comte", "Coolea", "Cooleney", "Coquetdale", "Corleggy", "Cornish Pepper",
            "Cotherstone", "Cotija", "Cottage Cheese", "Cottage Cheese (Australian)",
            "Cougar Gold", "Coulommiers", "Coverdale", "Crayeux de Roncq", "Cream Cheese",
            "Cream Havarti", "Crema Agria", "Crema Mexicana", "Creme Fraiche", "Crescenza",
            "Croghan", "Crottin de Chavignol", "Crottin du Chavignol", "Crowdie", "Crowley",
            "Cuajada", "Curd", "Cure Nantais", "Curworthy", "Cwmtawe Pecorino",
            "Cypress Grove Chevre", "Danablu (Danish Blue)", "Danbo", "Danish Fontina",
            "Daralagjazsky", "Dauphin", "Delice des Fiouves", "Denhany Dorset Drum", "Derby",
            "Dessertnyj Belyj", "Devon Blue", "Devon Garland", "Dolcelatte", "Doolin",
            "Doppelrhamstufel", "Dorset Blue Vinney", "Double Gloucester", "Double Worcester",
            "Dreux a la Feuille", "Dry Jack", "Duddleswell", "Dunbarra", "Dunlop", "Dunsyre Blue",
            "Duroblando", "Durrus", "Dutch Mimolette (Commissiekaas)", "Edam", "Edelpilz",
            "Emental Grand Cru", "Emlett", "Emmental", "Epoisses de Bourgogne", "Esbareich",
            "Esrom", "Etorki", "Evansdale Farmhouse Brie", "Evora De L'Alentejo", "Exmoor Blue",
            "Explorateur", "Feta", "Feta (Australian)", "Figue", "Filetta", "Fin-de-Siecle",
            "Finlandia Swiss", "Finn", "Fiore Sardo", "Fleur du Maquis", "Flor de Guia",
            "Flower Marie", "Folded", "Folded cheese with mint", "Fondant de Brebis",
            "Fontainebleau", "Fontal", "Fontina Val d'Aosta", "Formaggio di capra", "Fougerus",
            "Four Herb Gouda", "Fourme d' Ambert", "Fourme de Haute Loire", "Fourme de Montbrison",
            "Fresh Jack", "Fresh Mozzarella", "Fresh Ricotta", "Fresh Truffles", "Fribourgeois",
            "Friesekaas", "Friesian", "Friesla", "Frinault", "Fromage a Raclette", "Fromage Corse",
            "Fromage de Montagne de Savoie", "Fromage Frais", "Fruit Cream Cheese",
            "Frying Cheese", "Fynbo", "Gabriel", "Galette du Paludier", "Galette Lyonnaise",
            "Galloway Goat's Milk Gems", "Gammelost", "Gaperon a l'Ail", "Garrotxa", "Gastanberra",
            "Geitost", "Gippsland Blue", "Gjetost", "Gloucester", "Golden Cross", "Gorgonzola",
            "Gornyaltajski", "Gospel Green", "Gouda", "Goutu", "Gowrie", "Grabetto", "Graddost",
            "Grafton Village Cheddar", "Grana", "Grana Padano", "Grand Vatel",
            "Grataron d' Areches", "Gratte-Paille", "Graviera", "Greuilh", "Greve",
            "Gris de Lille", "Gruyere", "Gubbeen", "Guerbigny", "Halloumi",
            "Halloumy (Australian)", "Haloumi-Style Cheese", "Harbourne Blue", "Havarti",
            "Heidi Gruyere", "Hereford Hop", "Herrgardsost", "Herriot Farmhouse", "Herve",
            "Hipi Iti", "Hubbardston Blue Cow", "Hushallsost", "Iberico", "Idaho Goatster",
            "Idiazabal", "Il Boschetto al Tartufo", "Ile d'Yeu", "Isle of Mull", "Jarlsberg",
            "Jermi Tortes", "Jibneh Arabieh", "Jindi Brie", "Jubilee Blue", "Juustoleipa",
            "Kadchgall", "Kaseri", "Kashta", "Kefalotyri", "Kenafa", "Kernhem", "Kervella Affine",
            "Kikorangi", "King Island Cape Wickham Brie", "King River Gold", "Klosterkaese",
            "Knockalara", "Kugelkase", "L'Aveyronnais", "L'Ecir de l'Aubrac", "La Taupiniere",
            "La Vache Qui Rit", "Laguiole", "Lairobell", "Lajta", "Lanark Blue", "Lancashire",
            "Langres", "Lappi", "Laruns", "Lavistown", "Le Brin", "Le Fium Orbo", "Le Lacandou",
            "Le Roule", "Leafield", "Lebbene", "Leerdammer", "Leicester", "Leyden", "Limburger",
            "Lincolnshire Poacher", "Lingot Saint Bousquet d'Orb", "Liptauer", "Little Rydings",
            "Livarot", "Llanboidy", "Llanglofan Farmhouse", "Loch Arthur Farmhouse",
            "Loddiswell Avondale", "Longhorn", "Lou Palou", "Lou Pevre", "Lyonnais", "Maasdam",
            "Macconais", "Mahoe Aged Gouda", "Mahon", "Malvern", "Mamirolle", "Manchego",
            "Manouri", "Manur", "Marble Cheddar", "Marbled Cheeses", "Maredsous", "Margotin",
            "Maribo", "Maroilles", "Mascares", "Mascarpone", "Mascarpone (Australian)",
            "Mascarpone Torta", "Matocq", "Maytag Blue", "Meira", "Menallack Farmhouse",
            "Menonita", "Meredith Blue", "Mesost", "Metton (Cancoillotte)", "Meyer Vintage Gouda",
            "Mihalic Peynir", "Milleens", "Mimolette", "Mine-Gabhar", "Mini Baby Bells", "Mixte",
            "Molbo", "Monastery Cheeses", "Mondseer", "Mont D'or Lyonnais", "Montasio",
            "Monterey Jack", "Monterey Jack Dry", "Morbier", "Morbier Cru de Montagne",
            "Mothais a la Feuille", "Mozzarella", "Mozzarella (Australian)",
            "Mozzarella di Bufala", "Mozzarella Fresh, in water", "Mozzarella Rolls", "Munster",
            "Murol", "Mycella", "Myzithra", "Naboulsi", "Nantais", "Neufchatel",
            "Neufchatel (Australian)", "Niolo", "Nokkelost", "Northumberland", "Oaxaca",
            "Olde York", "Olivet au Foin", "Olivet Bleu", "Olivet Cendre",
            "Orkney Extra Mature Cheddar", "Orla", "Oschtjepka", "Ossau Fermier", "Ossau-Iraty",
            "Oszczypek", "Oxford Blue", "P'tit Berrichon", "Palet de Babligny", "Paneer", "Panela",
            "Pannerone", "Pant ys Gawn", "Parmesan (Parmigiano)", "Parmigiano Reggiano",
            "Pas de l'Escalette", "Passendale", "Pasteurized Processed", "Pate de Fromage",
            "Patefine Fort", "Pave d'Affinois", "Pave d'Auge", "Pave de Chirac", "Pave du Berry",
            "Pecorino", "Pecorino in Walnut Leaves", "Pecorino Romano", "Peekskill Pyramid",
            "Pelardon des Cevennes", "Pelardon des Corbieres", "Penamellera", "Penbryn",
            "Pencarreg", "Perail de Brebis", "Petit Morin", "Petit Pardou", "Petit-Suisse",
            "Picodon de Chevre", "Picos de Europa", "Piora", "Pithtviers au Foin",
            "Plateau de Herve", "Plymouth Cheese", "Podhalanski", "Poivre d'Ane", "Polkolbin",
            "Pont l'Eveque", "Port Nicholson", "Port-Salut", "Postel", "Pouligny-Saint-Pierre",
            "Pourly", "Prastost", "Pressato", "Prince-Jean", "Processed Cheddar", "Provolone",
            "Provolone (Australian)", "Pyengana Cheddar", "Pyramide", "Quark",
            "Quark (Australian)", "Quartirolo Lombardo", "Quatre-Vents", "Quercy Petit",
            "Queso Blanco", "Queso Blanco con Frutas --Pina y Mango", "Queso de Murcia",
            "Queso del Montsec", "Queso del Tietar", "Queso Fresco", "Queso Fresco (Adobera)",
            "Queso Iberico", "Queso Jalapeno", "Queso Majorero", "Queso Media Luna",
            "Queso Para Frier", "Queso Quesadilla", "Rabacal", "Raclette", "Ragusano", "Raschera",
            "Reblochon", "Red Leicester", "Regal de la Dombes", "Reggianito", "Remedou",
            "Requeson", "Richelieu", "Ricotta", "Ricotta (Australian)", "Ricotta Salata", "Ridder",
            "Rigotte", "Rocamadour", "Rollot", "Romano", "Romans Part Dieu", "Roncal", "Roquefort",
            "Roule", "Rouleau De Beaulieu", "Royalp Tilsit", "Rubens", "Rustinu", "Saaland Pfarr",
            "Saanenkaese", "Saga", "Sage Derby", "Sainte Maure", "Saint-Marcellin",
            "Saint-Nectaire", "Saint-Paulin", "Salers", "Samso", "San Simon", "Sancerre",
            "Sap Sago", "Sardo", "Sardo Egyptian", "Sbrinz", "Scamorza", "Schabzieger", "Schloss",
            "Selles sur Cher", "Selva", "Serat", "Seriously Strong Cheddar", "Serra da Estrela",
            "Sharpam", "Shelburne Cheddar", "Shropshire Blue", "Siraz", "Sirene", "Smoked Gouda",
            "Somerset Brie", "Sonoma Jack", "Sottocenare al Tartufo", "Soumaintrain",
            "Sourire Lozerien", "Spenwood", "Sraffordshire Organic", "St. Agur Blue Cheese",
            "Stilton", "Stinking Bishop", "String", "Sussex Slipcote", "Sveciaost", "Swaledale",
            "Sweet Style Swiss", "Swiss", "Syrian (Armenian String)", "Tala", "Taleggio", "Tamie",
            "Tasmania Highland Chevre Log", "Taupiniere", "Teifi", "Telemea", "Testouri",
            "Tete de Moine", "Tetilla", "Texas Goat Cheese", "Tibet", "Tillamook Cheddar",
            "Tilsit", "Timboon Brie", "Toma", "Tomme Brulee", "Tomme d'Abondance",
            "Tomme de Chevre", "Tomme de Romans", "Tomme de Savoie", "Tomme des Chouans", "Tommes",
            "Torta del Casar", "Toscanello", "Touree de L'Aubier", "Tourmalet",
            "Trappe (Veritable)", "Trois Cornes De Vendee", "Tronchon", "Trou du Cru", "Truffe",
            "Tupi", "Turunmaa", "Tymsboro", "Tyn Grug", "Tyning", "Ubriaco", "Ulloa",
            "Vacherin-Fribourgeois", "Valencay", "Vasterbottenost", "Venaco", "Vendomois",
            "Vieux Corse", "Vignotte", "Vulscombe", "Waimata Farmhouse Blue",
            "Washed Rind Cheese (Australian)", "Waterloo", "Weichkaese", "Wellington",
            "Wensleydale", "White Stilton", "Whitestone Farmhouse", "Wigmore", "Woodside Cabecou",
            "Xanadu", "Xynotyro", "Yarg Cornish", "Yarra Valley Pyramid", "Yorkshire Blue",
            "Zamorano", "Zanetti Grana Padano", "Zanetti Parmigiano Reggiano"
    };

    public static final String[] imgUrls = {"http://placesheen.com/100/700",
                                            "http://placesheen.com/600/400",
                                            "http://placesheen.com/400/800",
                                            "http://placesheen.com/400/300",
                                            "http://lorempixel.com/400/200/sports",
                                            "http://lorempixel.com/400/200/people",
                                            "http://lorempixel.com/400/200/technics",
                                            "http://lorempixel.com/400/300/city",
                                            "http://lorempixel.com/400/300/abstract",
                                            "http://lorempixel.com/400/300/transport",
                                            "http://lorempixel.com/400/300/food",
                                            "http://lorempixel.com/400/300/nature",
                                            "http://lorempixel.com/400/300/business",
                                            "http://lorempixel.com/400/300/nightlife",
                                            "http://lorempixel.com/400/300/sports",
                                            "http://lorempixel.com/400/300/technics",
                                            "http://lorempixel.com/400/350/transport",
                                            "http://lorempixel.com/400/350/food",
                                            "http://lorempixel.com/400/350/nature",
                                            "http://lorempixel.com/400/350/business",
                                            "http://lorempixel.com/400/350/nightlife",
                                            "http://lorempixel.com/400/350/sports",
                                            "http://lorempixel.com/400/350/technics",
                                            "http://lorempixel.com/300/300/transport",
                                            "http://lorempixel.com/300/300/food",
                                            "http://lorempixel.com/300/300/nature",
                                            "http://lorempixel.com/300/300/business",
                                            "http://lorempixel.com/300/300/nightlife",
                                            "http://lorempixel.com/300/300/sports",
                                            "http://lorempixel.com/300/300/technics",
                                            "http://lorempixel.com/300/350/transport",
                                            "http://lorempixel.com/300/350/food",
                                            "http://lorempixel.com/300/350/nature",
                                            "http://lorempixel.com/300/350/business",
                                            "http://lorempixel.com/300/350/nightlife",
                                            "http://lorempixel.com/300/350/sports",
                                            "http://lorempixel.com/300/350/technics",
                                            null,
                                            null,
                                            null};



    public static final PostType[] postTypes = PostType.values();

    public static final MessageType[] messageTypes = MessageType.values();

    public static final RepeatType[] repeatTypes = RepeatType.values();

    public static final ConnectionType[] connectionTypes = ConnectionType.values();

    public static final String[] tags = {"couch","soccer","running","free","help","tennis","car","donaudampfschiff","irreregulär","nsa","music","guitar","running","yoga","movies","cinema","bar","translation"};


    public static String getRandomFromField(String[] field){
        return field[((int)(Math.random()*100))%field.length];
    }

    public static PostType getRandomFromField(PostType[] field){
        return field[((int)(Math.random()*100))%field.length];
    }

    public static MessageType getRandomFromField(MessageType[] field){
        return field[((int)(Math.random()*100))%field.length];
    }

    public static RepeatType getRandomFromField(RepeatType[] field){
        return field[((int)(Math.random()*100))%field.length];
    }

    public static ConnectionType getRandomFromField(ConnectionType[] field) {
        return field[((int)(Math.random()*100))%field.length];
    }

    public static Post getRandomFromList(List<Post> list){
        if(list!=null && list.size()>0) {
            return list.get(((int)(Math.random()*100))%list.size());
        }else{
            return null;
        }
    }

    public static int getRandom(int upperBound){
        return getRandom(0, upperBound);
    }

    public static int getRandom(int lowerBound, int upperBound){
        Random rand = new Random();

        return rand.nextInt((upperBound - lowerBound) + 1) + lowerBound;
    }

    public static List<String> getRandomList(int upperBound, String[] field) {
        return getRandomList(0,upperBound,field);
    }

    public static List<String> getRandomList(int lowerBound, int upperBound, String[] field) {
        List<String> list = new ArrayList<String>();

        for(int i=0; i < lowerBound; i++) {
            list.add(getRandomFromField(field));
        }
        for(int i=0; i <= getRandom(upperBound-lowerBound); i++){
            list.add(getRandomFromField(field));
        }
        return list;
    }

    public static LatLng getRandomLocation(){
        DecimalFormat df = new DecimalFormat("0.000");

        double latitude = (Math.random() * (180 - (-180)) + (-180));
        double longitude = (Math.random() * (180 - (-180)) + (-180));

        return new LatLng(Double.valueOf(df.format(latitude)), Double.valueOf(df.format(longitude)));
    }

    //MOCK MODEL GENERATOR
    public static Post getRandomPost() {
       return new Post(getRandomFromField(postTypes), getRandomFromField(CHEESES), getLongRandomText(),getRandomList(25,tags),getRandom(100),getRandom(100),getRandom(100),getRandomList(6,imgUrls),getRandomFromField(imgUrls), getRandomLocation(), 0L, 0L, getRandomFromField(repeatTypes),getRandomBoolean());
    }

    public static Post getRandomMatch(){
        return new Post(getRandomFromField(postTypes), getRandomFromField(CHEESES), getLongRandomText(),getRandomList(25,tags),0,0,0,getRandomList(6,imgUrls),getRandomFromField(imgUrls), getRandomLocation(), 0L, 0L, getRandomFromField(repeatTypes),getRandomBoolean());
    }

    public static RequestListItemModel getRandomRequest(){
        return new RequestListItemModel(getRandomFromField(CHEESES));
    }

    public static String generateRandomImageUrl() {
        return "http://placesheen.com/"+getRandom(100,1600)+"/"+getRandom(100,1600);
    }

    public static boolean getRandomBoolean(){
        return ((int)(Math.random()*100))%3 == 0;
    }

    public static MessageItemModel getRandomMessage(){
        return getRandomMessage(getRandomFromField(messageTypes));
    }

    public static MessageItemModel getRandomMessage(MessageType messageType) {
        return new MessageItemModel(messageType,getRandomText());
    }

    public static ArrayList<MessageItemModel> getRandomMessages() {
        ArrayList<MessageItemModel> messageList = new ArrayList<MessageItemModel>();

        int amount = Mock.getRandom(0,100);
        messageList.add(getRandomMessage(MessageType.RECEIVE));

        for(int i = 0; i < amount; i++) {
            messageList.add(Mock.getRandomMessage());
        }
        return messageList;
    }

    public static String getRandomText(){
        List<String> wordList = getRandomList(1,15,CHEESES);
        StringBuilder message = new StringBuilder();

        for(String word : wordList){
            message.append(word).append(" ");
        }

        return message.toString();
    }

    public static String getLongRandomText(){
        List<String> wordList = getRandomList(1,40,CHEESES);
        StringBuilder message = new StringBuilder();

        for(String word : wordList){
            message.append(word).append(" ");
        }

        return message.toString();
    }

    public static void fillMyMockPosts(){
        int amount = 100;

        for(int i = 0; i < amount; i++) {
            Post post = Mock.getRandomPost();

            if(((int)(Math.random()*100)) < 10) {
                myMockPosts.put(post.getUuid(),post);
            }
        }
    }

    public static void fillMyMockMatches(){
        int amount = 250;

        for(int i = 0; i < amount; i++) {
            Post post = Mock.getRandomMatch();

            if(((int)(Math.random()*100)) < 10) {
                myMockMatches.put(post.getUuid(),post);
            }
        }
    }

    public static void fillMyMockConnections(){
        int amount = 500;

        for(int i = 0; i < amount; i++){
            Connection connection = new Connection(getRandomFromList(new ArrayList<Post>(myMockPosts.values())),getRandomFromList(new ArrayList<Post>(myMockMatches.values())),getRandomMessages(),getRandomFromField(connectionTypes));

            if(((int)(Math.random()*100)) < 10) {
                ArrayList<MessageItemModel> messages = new ArrayList<MessageItemModel>();

                switch(connection.getType()){
                    case CLOSED:
                        //DO NOTHING
                        break;
                    case SUGGESTED:
                        //SET MESSAGES TO ZERO
                        connection.setMessages(messages);
                        break;
                    case CONNECTED:
                        //DO NOTHING - MESSAGES CAN STAY
                        break;
                    case REQUEST_SENT:
                        messages.clear();
                        messages.add(getRandomMessage(MessageType.SEND));
                        connection.setMessages(messages);
                        break;
                    case REQUEST_RECEIVED:
                        messages.clear();
                        messages.add(getRandomMessage(MessageType.RECEIVE));
                        connection.setMessages(messages);
                        break;
                }
                myMockConversations.put(connection.getUuid(), connection);
            }
        }
    }

    public static ArrayList<Connection> getConversationsByPostId(UUID postId) {
        ArrayList<Connection> connections = new ArrayList<Connection>(myMockConversations.values());
        ArrayList<Connection> foundConnections = new ArrayList<Connection>();

        for(Connection connection : connections) {
            if(connection.getType() != ConnectionType.SUGGESTED && connection.getType() != ConnectionType.REQUEST_RECEIVED && connection.getMyPost().getUuid().equals(postId)){
                foundConnections.add(connection);
            }
        }
        return foundConnections;
    }

    public static ArrayList<Connection> getConversations() {
        ArrayList<Connection> connections = new ArrayList<Connection>(myMockConversations.values());
        ArrayList<Connection> foundConnections = new ArrayList<Connection>();

        for(Connection connection : connections) {
            if(connection.getType() != ConnectionType.SUGGESTED && connection.getType() != ConnectionType.REQUEST_RECEIVED){
                foundConnections.add(connection);
            }
        }
        return foundConnections;
    }

    public static ArrayList<Connection> getRequestsByPostId(UUID postId) {
        ArrayList<Connection> connections = new ArrayList<Connection>(myMockConversations.values());
        ArrayList<Connection> foundConnections = new ArrayList<Connection>();

        for(Connection connection : connections) {
            if(connection.getType() == ConnectionType.REQUEST_RECEIVED && (connection.getMyPost().getUuid().equals(postId))){
                foundConnections.add(connection);
            }
        }
        return foundConnections;
    }

    public static ArrayList<Post> getMatchesByPostId(UUID postId) {
        ArrayList<Connection> connections = new ArrayList<Connection>(myMockConversations.values());
        ArrayList<Post> matches = new ArrayList<Post>();

        for(Connection connection : connections) {
            if(connection.getType() == ConnectionType.REQUEST_RECEIVED && (connection.getMyPost().getUuid().equals(postId))){
                matches.add(connection.getMatchedPost());
            }
        }

        return matches;
    }

    public static ArrayList<MessageItemModel> getMessagesByConversationId(UUID conversationId) {
        Connection connection = myMockConversations.get(conversationId);

        if(connection != null){
            return new ArrayList<MessageItemModel>(connection.getMessages());
        }else{
            return new ArrayList<MessageItemModel>();
        }
    }

    public static void setNotificationCounters(){
        for(Post post : myMockPosts.values()){
            post.setMatches(Mock.getMatchesByPostId(post.getUuid()).size());
            post.setConversations(Mock.getConversationsByPostId(post.getUuid()).size());
            post.setRequests(Mock.getRequestsByPostId(post.getUuid()).size());
        }
    }
}
