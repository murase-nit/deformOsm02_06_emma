package mySrc.panel.inputPanel.osmCategoryMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * OSMのカテゴリ一覧
 * @author murase
 *
 */
public class CategoryListOsm {

//	public static final String[] category1 = {"Amenity", "shop", "tourism", "hiway", "railway"};
////	public static final String[] category1_yomi = {"施設", "店舗", "観光", "交通", "電車"};
//
//	public static final String[] category2a = {"all","cafe", "pub","restaurant","fast_food","hospital","toilets", "vending_machine", "place_of_worship", "fuel", "parking"};
//	public static final String[] category2b = {"all","convenience", "supermarket", "books"};
//	public static final String[] category2c = {"all","hotel"};
//	public static final String[] category2d = {"all","bus_stop"};
//	public static final String[] category2e = {"all","station"};
//	public static final String[] category2f = {""};
//	public static final String[] category2g = {""};
//	public static final String[] category2h = {""};

	public static final ArrayList<ArrayList<String>> categoryAll = new ArrayList<>();
	static {
		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","cafe")));
		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","fast_food")));
		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","pub")));
		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","restaurant")));
		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","parking")));
		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","hospital")));
		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","vending_machine")));
		
		categoryAll.add(new ArrayList<>(Arrays.asList("shop","convenience")));
		categoryAll.add(new ArrayList<>(Arrays.asList("shop","clothes")));
		categoryAll.add(new ArrayList<>(Arrays.asList("shop","supermarket")));
		categoryAll.add(new ArrayList<>(Arrays.asList("shop","book")));
		categoryAll.add(new ArrayList<>(Arrays.asList("highway","traffic_signals")));

//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","all")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","arts_centre")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","atm")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","bank")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","bar")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","bbq")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","bench")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","bicycle_parking")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","bicycle_rental")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","billiard")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","bus_station")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","cafe")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","car_rental")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","car_sharing")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","car_wash")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","charging_station")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","childcare")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","cinema")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","clinic")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","clock")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","college")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","community_centre")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","courthouse")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","crematorium")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","dental")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","dentist")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","doctors")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","drinking_water")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","driving_school")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","fast_food")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","ferry_terminal")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","fire_hydrant")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","fire_station")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","food_court")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","fountain")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","fuel")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","gambling")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","grave_yard")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","hospital")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","ice_cream")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","karaoke")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","karaoke_box")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","kindergarten")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","library")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","marketplace")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","massage")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","music_school")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","nightclub")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","nursing_home")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","parking")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","parking_entrance")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","parking_space")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","pharmacy")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","place_of_worship")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","police")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","post_box")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","post_office")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","prep_school")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","prison")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","pub")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","public_bath")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","public_building")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","recycling")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","restaurant")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","school")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","shelter")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","social_facility")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","studio")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","swimming_pool")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","taxi")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","telephone")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","theatre")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","toilets")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","townhall")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","training")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","truck_stop")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","university")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","vending_machine")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","veterinary")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","waste_basket")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","waste_disposal")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("Amenity","yes")));
//
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "all")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "alcohol")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "baby_goods")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "bakery")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "BBQ")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "beauty")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "bed")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "beverages")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "bicycle")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "books")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "boutique")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "butcher")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "camera")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "candy")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "car")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "car_parts")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "car_repair")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "chemist")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "cigar")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "clothes")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "computer")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "confectionery")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "convenience")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "deli")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "department_store")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "doityourself")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "dragstore")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "dry_cleaning")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "electronics")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "farm")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "florist")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "frame")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "furniture")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "garden_centre")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "general")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "gift")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "golf")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "greengrocer")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "hairdresser")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "hardware")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "hifi")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "hot-water")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "ice_cream")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "japanese tea")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "jewelry")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "kiosk")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "laundry")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "lottery")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "mall")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "massage")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "mobile_phone")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "money_lender")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "motorcycle")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "music")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "musical_instrument")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "newsagent")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "optician")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "organic")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "outdoor")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "party")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "pet")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "photo")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "rice")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "seafood")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "second hand")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "second_hand")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "shoes")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "shopping_centre")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "sports")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "stationery")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "supermarket")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "tailor")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "ticket")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "tobacco")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "toys")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "travel_agency")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "tyres")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "vacant")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "variety_shop")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "variety_store")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "video")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "video_games")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "wedding")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "yes")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "カラオケ")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("shop", "紳士服")));
//
//
//		categoryAll.add(new ArrayList<>(Arrays.asList("tourism", "all")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("tourism", "aquarium")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("tourism", "artwork")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("tourism", "attraction")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("tourism", "hostel")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("tourism", "hotel")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("tourism", "information")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("tourism", "motel")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("tourism", "museum")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("tourism", "picnic_site")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("tourism", "theme_park")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("tourism", "viewpoint")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("tourism", "yes")));
//		categoryAll.add(new ArrayList<>(Arrays.asList("tourism", "zoo")));
//
//		categoryAll.add(new ArrayList<>(Arrays.asList("highway", "traffic_signals")));

	}

}
