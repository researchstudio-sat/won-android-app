package at.researchstudio.sat.won.android.won_android_app.app.constants;

import android.os.Message;
import android.util.Log;
import at.researchstudio.sat.won.android.won_android_app.app.enums.MessageType;
import at.researchstudio.sat.won.android.won_android_app.app.enums.PostType;
import at.researchstudio.sat.won.android.won_android_app.app.enums.RepeatType;
import at.researchstudio.sat.won.android.won_android_app.app.model.*;
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
    public static Map<UUID, Conversation> myMockConversations = new HashMap<UUID, Conversation>();

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

    public static final String[] descriptions = {"Short Description",
                                                "This is a long description it might be cut \n\n off but it is not necessarily going to be cut off depending on the length of the text",
                                                "This is a long description it might be cut off but it is not necessarily going to be cut off depending on the length of the text cut off depending on the length of the text cut off depending on the length of the text",
                                                "This \n a \n short one",
                                                ""};

    public static final String[] imgUrls = {"http://placesheen.com/100/700",
                                            "http://placesheen.com/600/400",
                                            "http://placesheen.com/400/800",
                                            "http://placesheen.com/400/300",
                                            "http://lorempixel.com/400/200/sports",
                                            "http://lorempixel.com/400/200/people",
                                            "http://lorempixel.com/400/200/technics",
                                            null,
                                            null,
                                            null};



    public static final PostType[] postTypes = PostType.values();

    public static final MessageType[] messageTypes = MessageType.values();

    public static final RepeatType[] repeatTypes = RepeatType.values();

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
       return new Post(getRandomFromField(postTypes), getRandomFromField(CHEESES),getRandomFromField(descriptions),getRandomList(25,tags),getRandom(100),getRandom(100),getRandom(100),getRandomList(6,imgUrls),getRandomFromField(imgUrls), getRandomLocation(), 0L, 0L, getRandomFromField(repeatTypes));
    }

    public static Post getRandomMatch(){
        return new Post(getRandomFromField(postTypes), getRandomFromField(CHEESES),getRandomFromField(descriptions),getRandomList(25,tags),0,0,0,getRandomList(6,imgUrls),getRandomFromField(imgUrls), getRandomLocation(), 0L, 0L, getRandomFromField(repeatTypes));
    }

    public static ConversationListItemModel getRandomConversation(){
        return new ConversationListItemModel(getRandomFromField(CHEESES));
    }

    public static RequestListItemModel getRandomRequest(){
        return new RequestListItemModel(getRandomFromField(CHEESES));
    }

    public static String generateRandomImageUrl() {
        return "http://placesheen.com/"+getRandom(100,1600)+"/"+getRandom(100,1600);
    }

    public static MessageItemModel getRandomMessage(){
        return getRandomMessage(getRandomFromField(messageTypes));
    }

    public static MessageItemModel getRandomMessage(MessageType messageType) {
        return new MessageItemModel(messageType,getRandomText());
    }

    public static List<MessageItemModel> getRandomMessages() {
        ArrayList<MessageItemModel> messageList = new ArrayList<MessageItemModel>();

        int amount = Mock.getRandom(0,100);

        messageList.add(getRandomMessage(MessageType.RECEIVE)); //TODO: HAVE RECEIVED INIT CONVERSATIONS AND SENT RECEIVED CONVERSATIONS

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

    public static void fillMyMockPosts(){
        int amount = 1000;

        for(int i = 0; i < amount; i++) {
            Post post = Mock.getRandomPost();

            if(((int)(Math.random()*100)) == 0) {
                myMockPosts.put(post.getUuid(),post);
            }
        }
    }

    public static void fillMyMockMatches(){
        int amount = 2500;

        for(int i = 0; i < amount; i++) {
            Post post = Mock.getRandomMatch();

            if(((int)(Math.random()*100)) == 0) {
                myMockMatches.put(post.getUuid(),post);
            }
        }
    }

    public static void fillMyMockConversations(){
        int amount = 2500;

        for(int i = 0; i < amount; i++){
            Conversation conversation = new Conversation(getRandomFromList(new ArrayList<Post>(myMockPosts.values())),getRandomFromList(new ArrayList<Post>(myMockMatches.values())),getRandomMessages());

            if(((int)(Math.random()*100)) == 0) {
                myMockConversations.put(conversation.getUuid(), conversation);
            }
        }
    }

    public static ArrayList<Conversation> getConversationsByPostId(UUID postId) {
        ArrayList<Conversation> conversations = new ArrayList<Conversation>(myMockConversations.values());
        ArrayList<Conversation> foundConversations = new ArrayList<Conversation>();

        for(Conversation conversation : conversations) {
            if(conversation.getMyPost().getUuid().equals(postId)){
                foundConversations.add(conversation);
            }
        }
        return foundConversations;
    }

    public static ArrayList<MessageItemModel> getMessagesByConversationId(UUID conversationId) {
        Conversation conversation = myMockConversations.get(conversationId);

        if(conversation != null){
            return new ArrayList<MessageItemModel>(conversation.getMessages());
        }else{
            return new ArrayList<MessageItemModel>();
        }
    }
}
