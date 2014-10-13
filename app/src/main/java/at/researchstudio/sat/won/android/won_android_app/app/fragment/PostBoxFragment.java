package at.researchstudio.sat.won.android.won_android_app.app.fragment;

import android.app.*;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import at.researchstudio.sat.won.android.won_android_app.app.R;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.PostListItemAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.adapter.WelcomeScreenPagerAdapter;
import at.researchstudio.sat.won.android.won_android_app.app.components.LoadingDialog;
import at.researchstudio.sat.won.android.won_android_app.app.model.Post;
import at.researchstudio.sat.won.android.won_android_app.app.model.PostListItemModel;
import at.researchstudio.sat.won.android.won_android_app.app.model.PostType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fsuda on 21.08.2014.
 */
public class PostBoxFragment extends ListFragment {
    private static final String LOG_TAG = PostBoxFragment.class.getSimpleName();

    private CreateListTask createListTask;
    private ListView mNeedListView;
    private PostListItemAdapter mNeedListItemAdapter;

    private String postId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle args = getArguments();

        if(args!=null){
            postId=args.getString(Post.ID_REF);
            Log.d(LOG_TAG,"Fragment started with postId: "+postId);
        }
        Log.d(LOG_TAG,"postId: "+postId);

        mNeedListView = (ListView) inflater.inflate(R.layout.fragment_postbox, container, false);

        return mNeedListView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear(); //THIS IS ALL A LITTLE WEIRD STILL NOT SURE IF THIS IS AT ALL BEST PRACTICE
        getActivity().getMenuInflater().inflate(R.menu.needlist, menu);
        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchViewItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(LOG_TAG,"SEARCHQUERY: "+query);
                //TODO: INVOKE SEARCH
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(LOG_TAG,"SEARCHTEXT: "+newText);
                //TODO: CHANGE SEARCH RESULTS MAYBE
                return true;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        createListTask = new CreateListTask();
        createListTask.execute();
    }


    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy trying to cancel createListTask");
        super.onDestroy();
        if(createListTask != null && createListTask.getStatus() == AsyncTask.Status.RUNNING) {
            createListTask.cancel(true);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(LOG_TAG,"LIST ITEM CLICKED!!");
        //TODO: Implement "Real" list item clicking
        PostListItemModel needListItemModel = (PostListItemModel) mNeedListItemAdapter.getItem(position);
        Fragment fragment;

        Bundle args = new Bundle();

        if(!isMatchesList()) { //IF IT IS ONE OF YOUR OWN POSTS
            Log.d(LOG_TAG,"Clicked ownitem");
            postId=needListItemModel.getTitle();
            Log.d(LOG_TAG,"NEW POSTID: "+postId);
            args.putString(Post.ID_REF, postId); //TODO: CHANGE THIS TO ID

            fragment = new MyPostFragment();
        }else{ //IF ITS A POST FROM SOMEBODY ELSE
            //needListItemModel.setMatches(0);
            //mNeedListItemAdapter.notifyDataSetChanged();
            Log.d(LOG_TAG,"Clicked foreignitem");
            postId=needListItemModel.getTitle();
            Log.d(LOG_TAG,"NEW POSTID: "+postId);
            args.putString(Post.ID_REF, postId); //TODO: CHANGE THIS TO ID

            fragment = new PostFragment();
        }

        fragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    /**
     * Determines whether the Fragment is used for PostBox or for the Matches View
     * @return
     */
    public boolean isMatchesList(){
        return postId != null;
    }

    private class CreateListTask extends AsyncTask<String, Integer, ArrayList<PostListItemModel>> {
        private LoadingDialog progress;

        private final String[] CHEESES = {
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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(!isMatchesList()) { //SHOW LOADING DIALOG ONLY IN POSTBOX VIEW
                progress = new LoadingDialog(getActivity(), this);
                progress.show();

                progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        Log.d(LOG_TAG, "called onCancel");
                        if(createListTask != null && createListTask.getStatus() == AsyncTask.Status.RUNNING) {
                            createListTask.cancel(true);
                        }
                    }
                });
            }
        }

        @Override
        protected ArrayList<PostListItemModel> doInBackground(String... params) {

            ArrayList<PostListItemModel> retrievedList = new ArrayList<PostListItemModel>();

            int amount = 50000;

            //TODO: DUMMY DATA RETRIEVAL MOVE THIS TO THE BACKEND
            for(int i = 0; i < amount; i++) {
                if(isCancelled()){
                    Log.d(LOG_TAG, "GOT CANCELLED DURING BG WORK");
                    break;
                }

                int matches = (int)(Math.random()*100);

                if(matches > 50){
                    matches = 0;
                }

                int requests = (int)(Math.random()*100);

                if(requests > 50){
                    requests = 0;
                }

                int conversations = (int)(Math.random()*100);

                if(conversations > 50){
                    conversations = 0;
                }

                int tags = (int)(Math.random()*100);
                tags=tags%10;

                List<String> tagList = new ArrayList<String>();

                for(int j = 0; j < tags; j++){
                    tagList.add("tag "+j);
                }

                int imgResNr = (int)(Math.random()*100)%5;
                String imgUrl;

                String description;
                PostType type;

                switch(imgResNr) {
                    case 0:
                    default:
                        imgUrl = "http://placesheen.com/600/400";
                        description = "Short Description";
                        type = PostType.CHANGE;
                        break;
                    case 1:
                        imgUrl = "http://placesheen.com/100/700";
                        description = "S";
                        type = PostType.WANT;
                        break;
                    case 2:
                        imgUrl= "http://placesheen.com/400/800";
                        description = "This is a long description it might be cut \n\n off but it is not necessarily going to be cut off depending on the length of the text";
                        type = PostType.ACTIVITY;
                        break;
                    case 3:
                        imgUrl= "http://placesheen.com/400/300";
                        description = "This is a long description it might be cut off but it is not necessarily going to be cut off depending on the length of the text cut off depending on the length of the text cut off depending on the length of the text";
                        type = PostType.OFFER;
                        break;
                    case 4:
                        imgUrl= null;
                        description = "This \n a \n short one";
                        type = PostType.OFFER;
                        break;
                }
                String title = CHEESES[i%CHEESES.length];


                PostListItemModel need = new PostListItemModel(title, description, tagList, matches, requests, conversations, imgUrl, type);

                if(isMatchesList()) {
                    need = new PostListItemModel(title, description, tagList, 0, 0, 0, imgUrl, type);
                }
                if(((int)(Math.random()*1000)) == 0) {
                    retrievedList.add(need);
                }
            }

            return retrievedList;
        }

        @Override
        protected void onCancelled(ArrayList<PostListItemModel> linkArray) {
            Log.d(LOG_TAG, "ON CANCELED WAS CALLED");
            //TODO: INSERT CACHED RESULTS, WITHOUT CALL OF NEW THINGY
            if(linkArray != null) {
                mNeedListItemAdapter = new PostListItemAdapter(getActivity());
                for (PostListItemModel need : linkArray) {
                    mNeedListItemAdapter.addItem(need); //TODO: MOVE THIS TO THE BACKEND (OR ASYNC TASK ETC WHATEVER)
                }
                setListAdapter(mNeedListItemAdapter);
            }
        }

        protected void onPostExecute(ArrayList<PostListItemModel> linkArray) {
            mNeedListItemAdapter = new PostListItemAdapter(getActivity());
            for(PostListItemModel need : linkArray) {
                mNeedListItemAdapter.addItem(need); //TODO: MOVE THIS TO THE BACKEND (OR ASYNC TASK ETC WHATEVER)
            }
            setListAdapter(mNeedListItemAdapter);
            if(!isMatchesList()) { //DISMISS PROGRESS DIALOG ONLY IN POSTBOX VIEW
                progress.dismiss();
            }
        }
    }
}
