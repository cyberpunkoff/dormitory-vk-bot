package ru.mirea.edu.dormitorybot;

import api.longpoll.bots.LongPollBot;
import api.longpoll.bots.exceptions.VkApiException;
import api.longpoll.bots.model.events.messages.MessageNew;
import api.longpoll.bots.model.objects.basic.Message;
import lombok.SneakyThrows;


import java.util.LinkedHashMap;
import java.util.Map;

public class Main extends LongPollBot {
    @SneakyThrows

    public static void main(String[] args) {
        int[] a = {1, 3, 4};

        Object[] le;
        int[] array = new int[]{1, 3, 4, 3, 4};


//        int b = a[b.];




        Long groupId = 224833334L;


//
//        Gson gson = vk.getGson().newBuilder().setExclusionStrategies(new GsonExclusionStrategy()).create();
//        Field vkGsonField = vk.getClass().getDeclaredField("gson");
//        vkGsonField.setAccessible(true);
//        vkGsonField.set(vk, gson);

//        GroupActor actor = new GroupActor(groupId, accessToken);

//        MyCallback callback = new MyCallback(vk, actor, 30);


//        vk.groupsLongPoll().getLongPollServer(actor, groupId).execute();

//        callback.run();


        Main main = new Main();
        main.startPolling();



        Map<Integer, String> myMap = new LinkedHashMap<>();
    }

    @Override
    public void onMessageNew(MessageNew messageNew) {
        var message = messageNew.getMessage();

        var newmsg = new Message();
        newmsg.setId(message.getFromId());
        newmsg.setText("heel");


        try {
            var resp = vk.users.get()
                    .setUserIds(String.valueOf(message.getPeerId()))
                    .setFields("first_name")
                    .execute();

            var name = resp.getResponse().get(0).getFirstName();

            String rep = name + (name.equals("Василий") ? " сильный" : " слабый") + " java программист";

            vk.messages.send()
                    .setPeerId(message.getPeerId())
                    .setMessage(rep)
                    .execute();

        } catch (VkApiException e) {
            throw new RuntimeException(e);
        }




    }

    @Override
    public String getAccessToken() {
        String accessToken = "xz";

        return accessToken;
    }
}