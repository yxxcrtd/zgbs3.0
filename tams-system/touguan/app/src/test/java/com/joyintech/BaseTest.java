package com.joyintech;

import com.joyintech.tams.TamsApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author huwei1
 * @description 单元测试父类
 * @createDate 2022/6/2
 */
@SpringBootTest(classes = TamsApplication.class)
@ActiveProfiles("allinone")
@RunWith(SpringRunner.class)
public class BaseTest {
}
