package ru.itmo.web.hw4.util;

import ru.itmo.web.hw4.model.User;
import ru.itmo.web.hw4.model.Post;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DataUtil {
    private static final List<User> USERS = Arrays.asList(
            new User(1, "MikeMirzayanov", "Mike Mirzayanov", User.Color.GREEN),
            new User(6, "pashka", "Pavel Mavrin", User.Color.BLUE),
            new User(9, "geranazavr555", "Georgiy Nazarov",User.Color.RED),
            new User(11, "tourist", "Gennady Korotkevich", User.Color.BLUE)
    );

    private static final List<Post> POSTS = Arrays.asList(
            new Post(2, "The Benefits of Regular Exercise for Mental and Physical Health", "Regular exercise offers numerous benefits for both mental and physical health. It improves mood, reduces stress levels, and enhances cognitive function. Physical benefits include increased strength, flexibility, and cardiovascular health. Exercise also aids in weight management and reduces the risk of chronic diseases such as heart disease and diabetes. Incorporating regular exercise into your routine can lead to overall improved well-being and a higher quality of life", 1),
            new Post(17, "Head Shoulders Knees & Toes", '\n'+
                    "Head, shoulders, knees and toes,\n" +
                    "Knees and toes.\n" +
                    "\n" +
                    "Head, shoulders, knees and toes,\n" +
                    "Knees and toes.\n" +
                    "\n" +
                    "And eyes, and ears, and mouth,\n" +
                    "And nose.\n" +
                    "\n" +
                    "Head, shoulders, knees and toes,\n" +
                    "Knees and toes.", 1),
            new Post(7, "The Importance of Sleep for Optimal Brain Function and Well-being", "Sleep plays a crucial role in maintaining optimal brain function and overall well-being. It allows the brain to consolidate and process information, leading to improved learning and memory. Sufficient sleep also regulates mood and emotional well-being. Lack of sleep can impair cognitive abilities, increase the risk of accidents, and contribute to the development of mental health disorders. Prioritizing quality sleep through consistent sleep patterns, creating a conducive sleep environment, and practicing good sleep hygiene can have a profound positive impact on your mental and physical health.", 6),
            new Post(100, "The Beauty of Nature's Serenity", "Nature's serenity brings peacefulness. ", 9)
    );

    public static void addData(HttpServletRequest request, Map<String, Object> data) {
        data.put("users", USERS);
        data.put("posts", POSTS);

        for (User user : USERS) {
            if (Long.toString(user.getId()).equals(request.getParameter("logged_user_id"))) {
                data.put("user", user);
            }
        }
    }
}
