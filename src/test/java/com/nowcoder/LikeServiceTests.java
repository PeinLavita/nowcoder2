package com.nowcoder;

import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by yby on 2016/7/26.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
public class LikeServiceTests {
    @Autowired
    com.nowcoder.service.LikeService likeService;

    @Test
    public void testLike(){
        likeService.like(123, 1, 1);
        Assert.assertEquals(1,likeService.getLikeStatus(123, 1, 1));
       // Assert.assertEquals(-1,likeService.getLikeStatus(123, 1, 1));
    }

    @Test
    public void testB(){
        System.out.println("b");
    }

    @Test(expected = Exception.class)
    public void testC(){
        throw new IllegalArgumentException("testc");
    }

    @Before
    public void before(){
        System.out.println("before");
    }

    @After
    public void after(){
        System.out.println("after");
    }

    @BeforeClass
    public static void classBefore(){
        System.out.println("classBefore");
    }

    @AfterClass
    public static void classAfter(){
        System.out.println("afterClass");
    }
}
