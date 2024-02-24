package vn.kido.shop.Api;

import android.provider.Settings;

import com.google.gson.Gson;

import org.json.JSONArray;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;
import vn.kido.shop.Class.GlobalClass;
import vn.kido.shop.Constant.AppConfig;
import vn.kido.shop.Helper.StorageHelper;

public class APIService {
    private interface GateAPI {
        @GET("config/dev/info.json")
        Observable<String> getVersionUpdate();

        @GET("userservice/user/getregion")
        Observable<String> getRegion(@Query("type") int type,
                                     @Query("cityid") int cityId,
                                     @Query("districtid") int districtId);

        @POST("userservice/user/login")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> login(@Body String data);

        @POST("userservice/user/logout")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> logout(@Body String data);

        @POST("userservice/user/register")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> register(@Body String data);

        @POST("userservice/user/updateProfile")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> update(@Body String data);

        @POST("userservice/user/verify")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> verifyOTP(@Body String data);

        @GET("userservice/user/getProfile")
        Observable<String> getProfile(@Query("username") String username,
                                      @Query("deviceid") String deviceId,
                                      @Query("token") String token);

        @GET("productservice/product/get_banners")
        Observable<String> getBanners(@Query("username") String username,
                                      @Query("deviceid") String deviceId,
                                      @Query("token") String token);

        @GET("productservice/product/get_main_screen")
        Observable<String> getMainScreen(@Query("username") String username,
                                         @Query("deviceid") String deviceId,
                                         @Query("token") String token);

        @GET("productservice/product_type/get_type")
        Observable<String> getOrderType(@Query("username") String username,
                                        @Query("deviceid") String deviceId,
                                        @Query("token") String token);

        @GET("productservice/cate/get_cates")
        Observable<String> getCategories(@Query("username") String username,
                                         @Query("deviceid") String deviceId,
                                         @Query("token") String token,
                                         @Query("product_type_id") int product_type_id);

        @GET("productservice/product/get_products2")
        Observable<String> getProducts(
                @Query("username") String username,
                @Query("deviceid") String deviceId,
                @Query("token") String token,
                @Query("cate_id") int cateId,
                @Query("page") int page);

        @GET("productservice/product/get_detail")
        Observable<String> getDetailProduct(@Query("username") String username,
                                            @Query("deviceid") String deviceid,
                                            @Query("token") String token,
                                            @Query("id") int id);

        @GET("orderservice/order/get_list_order_history")
        Observable<String> getOrderHistories(@Query("username") String username,
                                             @Query("deviceid") String deviceid,
                                             @Query("token") String token,
                                             @Query("type") int type,
                                             @Query("sort_type") int sortType,
                                             @Query("page") int page);

        @GET("productservice/product/get_list_most_order")
        Observable<String> getOrderPopulars(@Query("username") String username,
                                            @Query("deviceid") String deviceid,
                                            @Query("token") String token,
                                            @Query("type") int type,
                                            @Query("sort_type") int sortType,
                                            @Query("page") int page);

        @GET("orderservice/order/get_follow_order")
        Observable<String> getOrderFollows(@Query("username") String username,
                                           @Query("deviceid") String deviceid,
                                           @Query("token") String token,
                                           @Query("status") int type,
                                           @Query("page") int page);

        @POST("userservice/noti/delete_noti")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> delete_noti(@Body String data);

        @GET("userservice/noti/get_noti_detail")
        Observable<String> getDetailNotify(@Query("username") String username,
                                           @Query("token") String token,
                                           @Query("deviceid") String deviceid,
                                           @Query("noti_id") int id);

        @GET("eventservice/event/get_event_details")
        Observable<String> getDetailEvent(@Query("username") String username,
                                          @Query("token") String token,
                                          @Query("deviceid") String deviceid,
                                          @Query("event_id") int id);

        @GET("newsservice/news/get_news")
        Observable<String> getNews(@Query("username") String username,
                                   @Query("token") String token,
                                   @Query("deviceid") String deviceid,
                                   @Query("type") int type);

        @GET("newsservice/news/get_news_detail")
        Observable<String> getDetailNew(@Query("username") String username,
                                        @Query("token") String token,
                                        @Query("deviceid") String deviceid,
                                        @Query("id") int id);

        @POST("newsservice/news/save_news")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> savenews(@Body String data);

        @POST("newsservice/news/remove_news_saved")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> deletesavenews(@Body String data);

        @POST("orderservice/order/add_product_to_cart")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> addProductToCart(@Body String data);

        @POST("orderservice/order/add_combo_to_cart")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> addComboToCart(@Body String data);

        @POST("orderservice/order/update_item_in_cart")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> updateProductToCart(@Body String data);


        @GET("orderservice/order/get_cart_3")
        Observable<String> getCart(@Query("username") String username,
                                   @Query("deviceid") String deviceid,
                                   @Query("token") String token);

        @POST("orderservice/order/remove_cart")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> removeCart(@Body String data);

        @POST("orderservice/order/remove_all")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> removeAllQuantity(@Body String data);

        @GET("userservice/user/user_level")
        Observable<String> getUserLevel(@Query("username") String username,
                                        @Query("deviceid") String deviceid,
                                        @Query("token") String token);

        @GET("userservice/user/get_gift")
        Observable<String> getKpoint(@Query("username") String username,
                                     @Query("deviceid") String deviceid,
                                     @Query("token") String token,
                                     @Query("type") int type);

        @POST("orderservice/order/confirm_book_order")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> confirmBookOrder(@Body String data);

        @POST("orderservice/order/book_order_2")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> bookOrder(@Body String data);

        @GET("orderservice/order/get_list_item_in_cart")
        Observable<String> getNumberCart(@Query("username") String username,
                                         @Query("deviceid") String deviceid,
                                         @Query("token") String token);

        @GET("orderservice/order/get_order_info_2")
        Observable<String> getOrderInfo(@Query("username") String username,
                                        @Query("deviceid") String deviceid,
                                        @Query("token") String token,
                                        @Query("order_code") String orderCode);

        @POST("orderservice/order/claim_reward_money")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> claimRewardMoney(@Body String data);

        @POST("orderservice/order/claim_reward_kpoint")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> claimRewardKpoint(@Body String data);

        @POST("orderservice/order/re_order")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> reOrder(@Body String data);

        @POST("orderservice/order/confirm_receive_order")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> confirmReceiveOrder(@Body String data);

        @POST("userservice/user/update_devicetoken")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> updateDeviceToken(@Body String data);

        @GET("eventservice/event/get_events")
        Observable<String> getEvent(@Query("username") String username,
                                    @Query("token") String token,
                                    @Query("deviceid") String deviceid,
                                    @Query("company_id") int companyId,
                                    @Query("page") int page);

        @GET("eventservice/event/get_event_running")
        Observable<String> getEventRunning(@Query("username") String username,
                                           @Query("token") String token,
                                           @Query("deviceid") String deviceid,
                                           @Query("id") int id,
                                           @Query("page") int page);

        @GET("userservice/noti/get_noti")
        Observable<String> getNotifies(@Query("username") String username,
                                       @Query("token") String token,
                                       @Query("deviceid") String deviceid,
                                       @Query("page") int page);

        @POST("userservice/user/active_push")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> activePush(@Body String data);

        @GET("userservice/user/get_help")
        Observable<String> getHelp(@Query("username") String username,
                                   @Query("deviceid") String deviceId,
                                   @Query("token") String tolken);

        @GET("userservice/user/get_help_detail")
        Observable<String> getHelpDetail(@Query("username") String username,
                                         @Query("deviceid") String deviceId,
                                         @Query("token") String tolken,
                                         @Query("id") int id);

        @GET("productservice/product/get_products_hot")
        Observable<String> getHighlightProduct(@Query("username") String username,
                                               @Query("deviceid") String deviceId,
                                               @Query("token") String token,
                                               @Query("page") int page);

        @GET("productservice/product/get_products_new")
        Observable<String> getNewProduct(@Query("username") String username,
                                         @Query("deviceid") String deviceId,
                                         @Query("token") String token,
                                         @Query("page") int page);

        @GET("productservice/cate/get_cate_by_parent")
        Observable<String> getCateById(@Query("username") String username,
                                       @Query("deviceid") String deviceId,
                                       @Query("token") String token,
                                       @Query("parent_id") int parentId);

        @GET("searchservice/search/search")
        Observable<String> search(@Query("username") String username,
                                  @Query("deviceid") String deviceId,
                                  @Query("token") String token,
                                  @Query("type") int type,
                                  @Query("keyword") String keyword,
                                  @Query("page") int page);

        @GET("searchservice/search/suggest_search")
        Observable<String> suggestSearch(@Query("username") String username,
                                         @Query("deviceid") String deviceId,
                                         @Query("token") String token,
                                         @Query("keyword") String keyword);

        @GET("userservice/noti/get_unseen")
        Observable<String> getUnSeen(@Query("username") String username,
                                     @Query("deviceid") String deviceId,
                                     @Query("token") String token);

        @POST("userservice/user/update_location")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> updateLocation(@Body String data);

        @GET("orderservice/order/get_feedback_info")
        Observable<String> getFeedbackInfo(
                @Query("username") String username,
                @Query("deviceid") String deviceId,
                @Query("token") String token,
                @Query("order_code") String orderCode);

        @POST("orderservice/order/add_event_to_cart")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> addEventToCart(@Body String data);

        @GET("eventservice/event/get_cate_events")
        Observable<String> getCompanyEvent(
                @Query("username") String username,
                @Query("deviceid") String deviceId,
                @Query("token") String token
        );

        @GET("orderservice/event/get_list_reward")
        Observable<String> getRewards(
                @Query("username") String username,
                @Query("deviceid") String deviceId,
                @Query("token") String token,
                @Query("event_reward_id") int id
        );

        @POST("orderservice/event/udate_event_reward")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> updateEventReward(@Body String data);

        @GET("orderservice/order/get_list_order_return")
        Observable<String> getListOrderReturn(
                @Query("username") String username,
                @Query("deviceid") String deviceId,
                @Query("token") String token
        );

        @GET("orderservice/order/return_order")
        Observable<String> getReasons(
                @Query("username") String username,
                @Query("deviceid") String deviceId,
                @Query("token") String token,
                @Query("order_code") String orderCode
        );

        @POST("orderservice/order/confirm_return_order")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> confirmReturn(@Body String data);

        @GET("orderservice/order/get_list_items_return")
        Observable<String> getRetails(
                @Query("username") String username,
                @Query("deviceid") String deviceId,
                @Query("token") String token
        );

        @POST("orderservice/order/book_return")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> bookReturn(@Body String data);

        @POST("orderservice/order/confirm_book_return")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> confirmBookReturn(@Body String data);

        @POST("userservice/user/send_otp")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> reSendOTP(@Body String data);

        @GET("userservice/user/get_app_config")
        Observable<String> getReturnPolicy(
                @Query("username") String username,
                @Query("deviceid") String deviceId,
                @Query("token") String token
        );

        @GET("orderservice/order/cancel_order")
        Observable<String> cancelOrder(
                @Query("username") String username,
                @Query("deviceid") String deviceId,
                @Query("token") String token,
                @Query("order_code") String orderCode
        );

        @POST("orderservice/order/confirm_cancel_order")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> confirmCancelOrder(@Body String data);

        @POST("userservice/noti/seen_noti")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> seenNoti(@Body String data);

        @GET("userservice/user/get_account_guest")
        Observable<String> getAccountGuest();

        @POST("userservice/user/verify_dms")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> verifyDms(@Body String data);

        @POST("userservice/user/register_2")
        @Headers({"Content-Type: application/json;charset=UTF-8"})
        Observable<String> registerCustomerCode(@Body String data);
    }


    private GateAPI gateAPI;
    private static APIService instance;

    public static APIService getInstance() {
        if (instance == null) {
            instance = new APIService();
        }
        return instance;
    }

    public static void setInstance(APIService value) {
        instance = value;
    }

    public APIService() {
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(AppConfig.DOMAIN)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            gateAPI = retrofit.create(GateAPI.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String deviceId;

    private String getDeviceId() {
        if (deviceId == null) {
            deviceId = Settings.Secure.getString(GlobalClass.getContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        }
        return deviceId;
    }

    public Observable<String> getVersionUpdate() {
        return gateAPI.getVersionUpdate();
    }

    public Observable<String> getRegion(int type, int idCity, int idDistrict) {
        return gateAPI.getRegion(type, idCity, idDistrict);
    }

    public Observable<String> login(String username) {
        Map map = new HashMap();
        map.put("username", username);
        map.put("deviceid", getDeviceId());
        map.put("os", "ANDROID");
        String data = new Gson().toJson(map);
        return gateAPI.login(data);
    }

    public Observable<String> logout(String username) {
        Map map = new HashMap();
        map.put("username", username);
        String data = new Gson().toJson(map);
        return gateAPI.logout(data);
    }

    public Observable<String> register(String username, String fullname,
                                       String shopName, String address, String avatar,
                                       int idCity, int idDistrict, int idWard) {
        Map map = new HashMap<>();
        map.put("username", username);
        map.put("fullname", fullname);
        map.put("shop_name", shopName);
        try {
            map.put("address", URLEncoder.encode(address, "UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put("avatar", avatar);
        map.put("cities_id", idCity);
        map.put("deviceid", StorageHelper.get(StorageHelper.DEVICE_ID));
        map.put("district_id", idDistrict);
        map.put("ward_id", idWard);
        map.put("version", AppConfig.VERSION);
        map.put("os", "ANDROID");
        String data = new Gson().toJson(map);
        return gateAPI.register(data);
    }

    public Observable<String> updateProfile(Map map) {
        //Map map = new HashMap<>();
        map.put("deviceid", StorageHelper.get(StorageHelper.DEVICE_ID));
        map.put("token", StorageHelper.get(StorageHelper.TOKEN));
        map.put("username", StorageHelper.get(StorageHelper.USERNAME));
        String data = new Gson().toJson(map);
        return gateAPI.update(data);
    }

    public Observable<String> verifyOTP(String username, String otp) {
        Map map = new HashMap();
        map.put("username", username);
        map.put("otp", otp);
        map.put("deviceid", getDeviceId());
        String data = new Gson().toJson(map);
        return gateAPI.verifyOTP(data);
    }

    public Observable<String> getProfile() {
        //return gateAPI.getProfile(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN));
        return gateAPI.getProfile(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN));
    }

    public Observable<String> getBanners() {
        return gateAPI.getBanners(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN));
    }

    public Observable<String> getMainScreen() {
        //return gateAPI.getMainScreen(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN));
        return gateAPI.getMainScreen(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN));
    }

    public Observable<String> getOrderType() {
        return gateAPI.getOrderType(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN));
    }

    public Observable<String> getCategories(int typeId) {
        return gateAPI.getCategories(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN), typeId);
    }

    public Observable<String> getProducts(int productTypeId, int page) {
        return gateAPI.getProducts(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN), productTypeId, page);
    }

    public Observable<String> getDetailProduct(int id) {
        return gateAPI.getDetailProduct(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN), id);
    }

    public Observable<String> getOrderHistories(int type, int sortType, int page) {
        return gateAPI.getOrderHistories(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN), type, sortType, page);
    }

    public Observable<String> getOrderPopulars(int type, int sortType, int page) {
        return gateAPI.getOrderPopulars(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN), type, sortType, page);
    }

    public Observable<String> getOrderFollows(int type, int page) {
        return gateAPI.getOrderFollows(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN), type, page);
    }

    public Observable<String> getDetailEvent(int id) {
        return gateAPI.getDetailEvent(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.TOKEN), StorageHelper.get(StorageHelper.DEVICE_ID), id);
    }

    public Observable<String> getDetailNotify(int id) {
        return gateAPI.getDetailNotify(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.TOKEN), StorageHelper.get(StorageHelper.DEVICE_ID), id);
    }

    public Observable<String> getNews(int type) {
        return gateAPI.getNews(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.TOKEN), StorageHelper.get(StorageHelper.DEVICE_ID), type);
    }

    public Observable<String> getDetailNew(int id) {
        return gateAPI.getDetailNew(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.TOKEN), StorageHelper.get(StorageHelper.DEVICE_ID), id);
    }

    public Observable<String> delete_notify(int iddelete) {
        Map map = new HashMap();
        map.put("username", StorageHelper.get(StorageHelper.USERNAME));
        map.put("token", StorageHelper.get(StorageHelper.TOKEN));
        map.put("deviceid", StorageHelper.get(StorageHelper.DEVICE_ID));
        map.put("noti_id", iddelete);
        String data = new Gson().toJson(map);
        return gateAPI.delete_noti(data);
    }

    public Observable<String> savenews(int idsavenews) {
        Map map = new HashMap();
        map.put("username", StorageHelper.get(StorageHelper.USERNAME));
        map.put("token", StorageHelper.get(StorageHelper.TOKEN));
        map.put("deviceid", StorageHelper.get(StorageHelper.DEVICE_ID));
        map.put("news_id", idsavenews);
        String data = new Gson().toJson(map);
        return gateAPI.savenews(data);
    }

    public Observable<String> deletesavenews(int iddeletesavenews) {
        Map map = new HashMap();
        map.put("username", StorageHelper.get(StorageHelper.USERNAME));
        map.put("token", StorageHelper.get(StorageHelper.TOKEN));
        map.put("deviceid", StorageHelper.get(StorageHelper.DEVICE_ID));
        map.put("news_id", iddeletesavenews);
        String data = new Gson().toJson(map);
        return gateAPI.deletesavenews(data);
    }

    public Observable<String> addProductToCart(int id, int quantity, int typeUnit, JSONArray attribute) {
        Map map = new HashMap();
        map.put("username", StorageHelper.get(StorageHelper.USERNAME));
        map.put("token", StorageHelper.get(StorageHelper.TOKEN));
        map.put("deviceid", StorageHelper.get(StorageHelper.DEVICE_ID));
        map.put("product_id", id);
        map.put("quantity", quantity);
        map.put("type_unit", typeUnit);
        map.put("attribute", attribute.toString());
        return gateAPI.addProductToCart(new Gson().toJson(map));
    }

    public Observable<String> updateProductToCart(int id, int quantity, int attributeId) {
        Map map = new HashMap();
        map.put("username", StorageHelper.get(StorageHelper.USERNAME));
        map.put("token", StorageHelper.get(StorageHelper.TOKEN));
        map.put("deviceid", StorageHelper.get(StorageHelper.DEVICE_ID));
        map.put("id", id);
        map.put("quantity", quantity);
        map.put("attribute_id", attributeId);
        return gateAPI.updateProductToCart(new Gson().toJson(map));
    }

    public Observable<String> addComboToCart(int id, int quantity) {
        Map map = new HashMap();
        map.put("username", StorageHelper.get(StorageHelper.USERNAME));
        map.put("token", StorageHelper.get(StorageHelper.TOKEN));
        map.put("deviceid", StorageHelper.get(StorageHelper.DEVICE_ID));
        map.put("combo_id", id);
        map.put("quantity", quantity);
        return gateAPI.addComboToCart(new Gson().toJson(map));
    }

    public Observable<String> getCart() {
        return gateAPI.getCart(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN));
    }

    public Observable<String> removeCart(int id, int type, int quantity) {
        Map map = new HashMap();
        map.put("username", StorageHelper.get(StorageHelper.USERNAME));
        map.put("token", StorageHelper.get(StorageHelper.TOKEN));
        map.put("deviceid", StorageHelper.get(StorageHelper.DEVICE_ID));
        map.put("id", id);
        map.put("type", type);
        map.put("quantity", quantity);
        return gateAPI.removeCart(new Gson().toJson(map));
    }

    public Observable<String> removeAllQuantity(int id, int type) {
        Map map = new HashMap();
        map.put("username", StorageHelper.get(StorageHelper.USERNAME));
        map.put("token", StorageHelper.get(StorageHelper.TOKEN));
        map.put("deviceid", StorageHelper.get(StorageHelper.DEVICE_ID));
        map.put("id", id);
        map.put("type", type);
        return gateAPI.removeAllQuantity(new Gson().toJson(map));
    }

    public Observable<String> getUserLevel() {
        return gateAPI.getUserLevel(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN));
    }

    public Observable<String> getKpoint(int type) {
        return gateAPI.getKpoint(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN), type);
    }

    public Observable<String> confirmBookOrder(String deliveryNote, String fullname, String address, String phone, String taxCode, int cityId, int districtId, int wardId) {
        Map map = new HashMap();
        map.put("username", StorageHelper.get(StorageHelper.USERNAME));
        map.put("token", StorageHelper.get(StorageHelper.TOKEN));
        map.put("deviceid", StorageHelper.get(StorageHelper.DEVICE_ID));
        map.put("delivery_note", deliveryNote);
        map.put("fullname", fullname);
        map.put("address", address);
        map.put("phone", phone);
        map.put("tax_code", taxCode);
        map.put("city_id", cityId);
        map.put("district_id", districtId);
        map.put("ward_id", wardId);
        return gateAPI.confirmBookOrder(new Gson().toJson(map));
    }

    public Observable<String> bookOrder() {
        Map map = new HashMap();
        map.put("username", StorageHelper.get(StorageHelper.USERNAME));
        map.put("token", StorageHelper.get(StorageHelper.TOKEN));
        map.put("deviceid", StorageHelper.get(StorageHelper.DEVICE_ID));
        return gateAPI.bookOrder(new Gson().toJson(map));
    }

    public Observable<String> claimRewardMoney(int rewardId) {
        Map map = new HashMap();
        map.put("username", StorageHelper.get(StorageHelper.USERNAME));
        map.put("token", StorageHelper.get(StorageHelper.TOKEN));
        map.put("deviceid", StorageHelper.get(StorageHelper.DEVICE_ID));
        map.put("reward_id", rewardId);
        return gateAPI.claimRewardMoney(new Gson().toJson(map));
    }

    public Observable<String> claimRewardKpoint(int rewardId) {
        Map map = new HashMap();
        map.put("username", StorageHelper.get(StorageHelper.USERNAME));
        map.put("token", StorageHelper.get(StorageHelper.TOKEN));
        map.put("deviceid", StorageHelper.get(StorageHelper.DEVICE_ID));
        map.put("reward_id", rewardId);
        return gateAPI.claimRewardKpoint(new Gson().toJson(map));
    }

    public Observable<String> getNumberCart() {
        //return gateAPI.getNumberCart(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN));
        return gateAPI.getNumberCart(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN));
    }


    public Observable<String> getOrderInfo(String orderCode) {
        return gateAPI.getOrderInfo(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN), orderCode);
    }

    public Observable<String> reOrder(String orderCode) {
        Map map = new HashMap();
        map.put("username", StorageHelper.get(StorageHelper.USERNAME));
        map.put("token", StorageHelper.get(StorageHelper.TOKEN));
        map.put("deviceid", StorageHelper.get(StorageHelper.DEVICE_ID));
        map.put("order_code", orderCode);
        return gateAPI.reOrder(new Gson().toJson(map));

    }

    public Observable<String> confirmReceiveOrder(String orderCode, int rate, String rateOrder) {
        Map map = new HashMap();
        map.put("username", StorageHelper.get(StorageHelper.USERNAME));
        map.put("token", StorageHelper.get(StorageHelper.TOKEN));
        map.put("deviceid", StorageHelper.get(StorageHelper.DEVICE_ID));
        map.put("order_code", orderCode);
        map.put("rate", rate);
        map.put("rate_order", rateOrder);
        return gateAPI.confirmReceiveOrder(new Gson().toJson(map));
    }

    public Observable<String> updateDeviceToken(String firebaseToken) {
        Map map = new HashMap();
        map.put("username", StorageHelper.get(StorageHelper.USERNAME));
        //map.put("deviceid", StorageHelper.get(StorageHelper.DEVICE_ID));
        map.put("deviceid", StorageHelper.get(StorageHelper.DEVICE_ID));
        map.put("os", "ANDROID");
        map.put("token", StorageHelper.get(StorageHelper.TOKEN));
        map.put("firebase_devicetoken", firebaseToken);
        return gateAPI.updateDeviceToken(new Gson().toJson(map));
    }

    public Observable<String> getEvent(int companyId, int page) {
        return gateAPI.getEvent(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.TOKEN), StorageHelper.get(StorageHelper.DEVICE_ID), companyId, page);
    }

    public Observable<String> getEventRunning(int id, int page) {
        return gateAPI.getEventRunning(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.TOKEN), StorageHelper.get(StorageHelper.DEVICE_ID), id, page);
    }

    public Observable<String> getNotifies(int page) {
        return gateAPI.getNotifies(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.TOKEN), StorageHelper.get(StorageHelper.DEVICE_ID), page);
    }

    public Observable<String> activePush(int active) {
        Map map = new HashMap();
        map.put("username", StorageHelper.get(StorageHelper.USERNAME));
        map.put("token", StorageHelper.get(StorageHelper.TOKEN));
        map.put("deviceid", StorageHelper.get(StorageHelper.DEVICE_ID));
        map.put("active", active);
        return gateAPI.activePush(new Gson().toJson(map));
    }

    public Observable<String> getHelp() {
        return gateAPI.getHelp(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN));
    }

    public Observable<String> getHelpDetail(int id) {
        return gateAPI.getHelpDetail(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN), id);
    }

    public Observable<String> getHighlightProduct(int page) {
        return gateAPI.getHighlightProduct(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN), page);
    }

    public Observable<String> getNewProduct(int page) {
        return gateAPI.getNewProduct(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN), page);
    }

    public Observable<String> getCatById(int id) {
        return gateAPI.getCateById(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN), id);
    }

    public Observable<String> search(int type, String keyword, int page) {
        return gateAPI.search(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN), type, keyword, page);
    }

    public Observable<String> suggestSearch(String keyWord) {
        return gateAPI.suggestSearch(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN), keyWord);
    }

    public Observable<String> getUnSeen() {
        //return gateAPI.getUnSeen(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN));
        return gateAPI.getUnSeen(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN));
    }

    public Observable<String> updateLocation(int type, double lat, double lng) {
        Map map = new HashMap();
        map.put("username", StorageHelper.get(StorageHelper.USERNAME));
        map.put("deviceid", StorageHelper.get(StorageHelper.DEVICE_ID));
        map.put("token", StorageHelper.get(StorageHelper.TOKEN));
        map.put("type", type);
        map.put("lat", lat);
        map.put("long", lng);
        return gateAPI.updateLocation(new Gson().toJson(map));
    }

    public Observable<String> getFeedbackInfo(String orderCode) {
        return gateAPI.getFeedbackInfo(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN), orderCode);
    }

    public Observable<String> addEventToCart(int eventId) {
        Map map = new HashMap();
        map.put("username", StorageHelper.get(StorageHelper.USERNAME));
        map.put("token", StorageHelper.get(StorageHelper.TOKEN));
        map.put("deviceid", StorageHelper.get(StorageHelper.DEVICE_ID));
        map.put("event_id", eventId);
        return gateAPI.addEventToCart(new Gson().toJson(map));
    }

    public Observable<String> getCompanyEvent() {
        return gateAPI.getCompanyEvent(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN));
    }

    public Observable<String> getRewards(int id) {
        return gateAPI.getRewards(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN), id);
    }

    public Observable<String> updateEventReward(int idEvent, int idProduct, int quantity) {
        Map map = new HashMap();
        map.put("username", StorageHelper.get(StorageHelper.USERNAME));
        map.put("token", StorageHelper.get(StorageHelper.TOKEN));
        map.put("deviceid", StorageHelper.get(StorageHelper.DEVICE_ID));
        map.put("event_reward_id", idEvent);
        map.put("item_reward_id", idProduct);
        map.put("item_reward_quantity", quantity);
        return gateAPI.updateEventReward(new Gson().toJson(map));
    }

    public Observable<String> getReasons(String orderCode) {
        return gateAPI.getReasons(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN), orderCode);
    }

    public Observable<String> confirmReturn(String orderCode, String note, String reason) {
        Map map = new HashMap();
        map.put("username", StorageHelper.get(StorageHelper.USERNAME));
        map.put("token", StorageHelper.get(StorageHelper.TOKEN));
        map.put("deviceid", StorageHelper.get(StorageHelper.DEVICE_ID));
        map.put("order_code", orderCode);
        map.put("note", note);
        map.put("reason", reason);
        return gateAPI.confirmReturn(new Gson().toJson(map));
    }

    public Observable<String> getListOrderReturn() {
        return gateAPI.getListOrderReturn(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN));
    }

    public Observable<String> getRetails() {
        return gateAPI.getRetails(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN));
    }

    public Observable<String> bookReturn(String data) {
        Map map = new HashMap();
        map.put("username", StorageHelper.get(StorageHelper.USERNAME));
        map.put("token", StorageHelper.get(StorageHelper.TOKEN));
        map.put("deviceid", StorageHelper.get(StorageHelper.DEVICE_ID));
        map.put("items", data);
        return gateAPI.bookReturn(new Gson().toJson(map));
    }

    public Observable<String> confirmBookReturn(int type, String data) {
        Map map = new HashMap();
        map.put("username", StorageHelper.get(StorageHelper.USERNAME));
        map.put("token", StorageHelper.get(StorageHelper.TOKEN));
        map.put("deviceid", StorageHelper.get(StorageHelper.DEVICE_ID));
        map.put("type", type);
        map.put("items", data);
        return gateAPI.confirmBookReturn(new Gson().toJson(map));
    }

    public Observable<String> reSendOTP(String username) {
        Map map = new HashMap();
        map.put("username", username);
        return gateAPI.reSendOTP(new Gson().toJson(map));
    }

    public Observable<String> getReturnPolicy() {
        return gateAPI.getReturnPolicy(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN));
    }

    public Observable<String> getReasonCancel(String orderCode) {
        return gateAPI.cancelOrder(StorageHelper.get(StorageHelper.USERNAME), StorageHelper.get(StorageHelper.DEVICE_ID), StorageHelper.get(StorageHelper.TOKEN), orderCode);
    }

    public Observable<String> confirmCancelOrder(String orderCode, String note, String reason) {
        Map map = new HashMap();
        map.put("username", StorageHelper.get(StorageHelper.USERNAME));
        map.put("token", StorageHelper.get(StorageHelper.TOKEN));
        map.put("deviceid", StorageHelper.get(StorageHelper.DEVICE_ID));
        map.put("order_code", orderCode);
        map.put("note", note);
        map.put("reason", reason);
        return gateAPI.confirmCancelOrder(new Gson().toJson(map));
    }

    public Observable<String> seenNoti(int notiId) {
        Map map = new HashMap();
        map.put("username", StorageHelper.get(StorageHelper.USERNAME));
        map.put("token", StorageHelper.get(StorageHelper.TOKEN));
        map.put("deviceid", StorageHelper.get(StorageHelper.DEVICE_ID));
        map.put("noti_id", notiId);
        return gateAPI.seenNoti(new Gson().toJson(map));
    }

    public Observable<String> getAccountGuest() {
        return gateAPI.getAccountGuest();
    }

    public Observable<String> verifyDms(String dms, String username, String token, String fullName, String shopName) {
        Map map = new HashMap();
        map.put("username", username);
        map.put("token", token);
        map.put("deviceid", getDeviceId());
        map.put("dms", dms);
        map.put("full_name", fullName);
        map.put("shop_name", shopName);
        return gateAPI.verifyDms(new Gson().toJson(map));
    }

    public Observable<String> registerCustomerCode(String phone, String fullname, String shopName, String dms) {
        Map map = new HashMap<>();
        map.put("phone", phone);
        map.put("fullname", fullname);
        map.put("deviceid", getDeviceId());
        map.put("shop_name", shopName);
//        try {
//            map.put("shop_name", URLEncoder.encode(shopName, "UTF-8"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        map.put("os", "ANDROID");
        map.put("dms", dms);
        String data = new Gson().toJson(map);
        return gateAPI.registerCustomerCode(data);
    }
}
