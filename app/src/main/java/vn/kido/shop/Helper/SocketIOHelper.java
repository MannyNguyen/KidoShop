package vn.kido.shop.Helper;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import vn.kido.shop.Bean.BeanMessage;
import vn.kido.shop.Class.GlobalClass;
import vn.kido.shop.Fragment.Support.MenuSupportFragment;
import vn.kido.shop.Fragment.Support.SupportFragment;

public class SocketIOHelper {
    private static final String JOIN_ROOM_RESPONSE = "join_room_response";
    private static final String SERVER_TEXT_MESSAGE = "server_text_message";
    private static final String SERVER_DEFAULT_MESSAGE = "server_default_message";
    private static final String SERVER_IMAGE_MESSAGE = "server_image_message";
    private static final String SERVER_HISTORY_MESSAGE = "server_history_message";

    public static String HOST = "";
    private Socket socket;

    //region INIT
    private static SocketIOHelper instance;

    public static SocketIOHelper getInstance() {
        if (instance == null) {
            instance = new SocketIOHelper();
        }
        return instance;
    }

    public void resetSocket() {
        socket = null;
    }

    public Socket getSocket() {
        try {
            if (socket == null) {
                IO.Options option = new IO.Options();
                option.transports = new String[]{"websocket"};
                socket = IO.socket(HOST, option);
            }
            return socket;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //endregion

    //region RESPONSE
    public static void connect() {
        if (SocketIOHelper.getInstance().getSocket() == null) {
            return;
        }
        SocketIOHelper.getInstance().getSocket()
                .on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                    @Override
                    public void call(Object... args) {
                        try {
                            Map map = new HashMap();
                            map.put("username", StorageHelper.get(StorageHelper.USERNAME));
                            SocketIOHelper.getInstance().getSocket().emit("join_room", new Gson().toJson(map));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                })
                .on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        int a = 1;

                    }
                })
                .on(JOIN_ROOM_RESPONSE, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        GlobalClass.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SupportFragment supportFragment = (SupportFragment) FragmentHelper.findFragmentByTag(SupportFragment.class);
                                if (supportFragment != null) {
                                    supportFragment.messages.clear();
                                }
                                SocketIOHelper.getInstance().getHistory(0);
                            }
                        });

                    }
                })
                .on(SERVER_TEXT_MESSAGE, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        try {
                            SocketIOHelper.getInstance().onText((String) args[0]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .on(SERVER_IMAGE_MESSAGE, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        try {
                            SocketIOHelper.getInstance().onImage((String) args[0]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .on(SERVER_DEFAULT_MESSAGE, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        SocketIOHelper.getInstance().onDefault((String) args[0]);
                    }
                })
                .on(SERVER_HISTORY_MESSAGE, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        SocketIOHelper.getInstance().onHistory((String) args[0]);
                    }
                })
                .on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.d("SOCKET", "disconnect");
                        SocketIOHelper.getInstance().resetSocket();
                    }

                });
        SocketIOHelper.getInstance().getSocket().connect();
    }

    public void disconnect() {
        if (SocketIOHelper.getInstance().getSocket() == null) {
            return;
        }
        SocketIOHelper.getInstance().getSocket().disconnect();

    }

    private void onText(String data) {
        final BeanMessage beanMessage = new Gson().fromJson(data, BeanMessage.class);
        beanMessage.setMe(BeanMessage.NOTME);
        beanMessage.setType(BeanMessage.TEXT);
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SupportFragment supportFragment = (SupportFragment) FragmentHelper.findFragmentByTag(SupportFragment.class);
                if (supportFragment != null) {
                    supportFragment.messages.add(beanMessage);
                    supportFragment.adapter.notifyItemInserted(supportFragment.messages.size() - 1);
                    supportFragment.recycler.smoothScrollToPosition(supportFragment.messages.size() - 1);
                }
            }
        });
    }

    private void onImage(String data) {
        final BeanMessage beanMessage = new Gson().fromJson(data, BeanMessage.class);
        beanMessage.setMe(BeanMessage.NOTME);
        beanMessage.setType(BeanMessage.IMAGE);
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SupportFragment supportFragment = (SupportFragment) FragmentHelper.findFragmentByTag(SupportFragment.class);
                if (supportFragment != null) {
                    supportFragment.messages.add(beanMessage);
                    supportFragment.adapter.notifyItemInserted(supportFragment.messages.size() - 1);
                    supportFragment.recycler.smoothScrollToPosition(supportFragment.messages.size() - 1);
                }
            }
        });
    }

    private void onDefault(String data) {
        final BeanMessage beanMessage = new Gson().fromJson(data, BeanMessage.class);
        beanMessage.setMe(BeanMessage.NOTME);
        beanMessage.setType(BeanMessage.DEFAULT);
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Fragment fragment = FragmentHelper.getActiveFragment(GlobalClass.getActivity());
                if (fragment instanceof MenuSupportFragment) {
                    MenuSupportFragment menuSupportFragment = (MenuSupportFragment) fragment;
                    for (BeanMessage.BeanChildMessage item : beanMessage.getChild()) {
                        menuSupportFragment.items.add(item);
                        menuSupportFragment.adapter.notifyDataSetChanged();
                    }
                    return;
                }
                SupportFragment supportFragment = (SupportFragment) FragmentHelper.findFragmentByTag(SupportFragment.class);
                if (supportFragment != null) {
                    supportFragment.messages.add(beanMessage);
                    supportFragment.adapter.notifyItemInserted(supportFragment.messages.size() - 1);
                    supportFragment.recycler.smoothScrollToPosition(supportFragment.messages.size() - 1);
                }
            }
        });
    }

    private void onHistory(final String data) {
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    SupportFragment supportFragment = (SupportFragment) FragmentHelper.findFragmentByTag(SupportFragment.class);
                    if (supportFragment != null) {
                        JSONArray arr = new JSONObject(data).getJSONArray("history");
                        if (arr.length() > 0) {
                            for (int i = arr.length() - 1; i >= 0; i--) {
                                final BeanMessage beanMessage = new Gson().fromJson(arr.getString(i), BeanMessage.class);
                                supportFragment.messages.add(0, beanMessage);
                                supportFragment.adapter.notifyItemInserted(0);
                            }
                            //supportFragment.adapter.notifyDataSetChanged();
                            supportFragment.recycler.addOnScrollListener(supportFragment.onScrollListener);
                        } else {
                            if (supportFragment.messages.size() > 0) {
                                return;
                            }
                            SocketIOHelper.getInstance().sendMessage(BeanMessage.DEFAULT, "", 1);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }
    //endregion

    //region REQUEST
    public void sendMessage(int type, Object message) {
        Map map = new HashMap();
        map.put("username", StorageHelper.get(StorageHelper.USERNAME));
        map.put("type", type);
        map.put("message", message);
        SocketIOHelper.getInstance().getSocket().emit("client_message", new Gson().toJson(map));
    }


    public void sendMessage(int type, Object message, int width, int height) {
        Map map = new HashMap();
        map.put("username", StorageHelper.get(StorageHelper.USERNAME));
        map.put("type", type);
        map.put("message", message);
        map.put("width", width);
        map.put("height", height);
        SocketIOHelper.getInstance().getSocket().emit("client_message", new Gson().toJson(map));
    }

    public void sendMessage(int type, Object message, int id) {
        Map map = new HashMap();
        map.put("username", StorageHelper.get(StorageHelper.USERNAME));
        map.put("type", type);
        map.put("message", message);
        map.put("id", id);
        SocketIOHelper.getInstance().getSocket().emit("client_message", new Gson().toJson(map));
    }

    public void getHistory(int firstId) {
        GlobalClass.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SupportFragment supportFragment = (SupportFragment) FragmentHelper.findFragmentByTag(SupportFragment.class);
                if (supportFragment != null) {
                    supportFragment.recycler.removeOnScrollListener(supportFragment.onScrollListener);
                }
            }
        });
        Map map = new HashMap();
        map.put("username", StorageHelper.get(StorageHelper.USERNAME));
        map.put("lastMessageID", firstId);
        SocketIOHelper.getInstance().getSocket().emit("get_history", new Gson().toJson(map));
    }
    //endregion

}
