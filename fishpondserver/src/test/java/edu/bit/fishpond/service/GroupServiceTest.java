package edu.bit.fishpond.service;

import com.alibaba.fastjson.JSONObject;
import edu.bit.fishpond.DAO.ServiceDao;
import edu.bit.fishpond.service.entity.GroupCreateClientEntity;
import edu.bit.fishpond.service.entity.GroupInfoServerEntity;
import edu.bit.fishpond.utils.DAOException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GroupServiceTest {

    @Autowired
    GroupService groupService;

    @MockBean
    private ServiceDao serviceDao;

    private List<ServerMessage> serverMessageList;

    @Before
    public void BeforeEachMethod(){
        serverMessageList = new ArrayList<>();
    }

    /**
     * 测试群聊创建业务，即groupCreateHandler方法在正常情况下的状态
     * @throws DAOException 数据库异常
     */
    @Test(timeout = 150)
    public void groupCreateHandlerTest() throws DAOException {
        //Set:通信层输入
        GroupCreateClientEntity entity = new GroupCreateClientEntity();
        entity.setGroupName("地平线小组");
        entity.setCreatorId(20181575);
        List<Integer> initialMemberList = new ArrayList<>();
        initialMemberList.add(20181575);
        initialMemberList.add(20182906);
        initialMemberList.add(20173488);
        entity.setInitialMembers(initialMemberList);

        //Set:数据层输入
        when(serviceDao.recordNewGroup(eq(entity.getGroupName()), eq(entity.getCreatorId()), anyString())).
                thenReturn(1234);
        when(serviceDao.queryOnlineStatusById(anyInt())).thenReturn(true);

        serverMessageList = groupService.groupCreateHandler(entity);

        //Test:要发送的消息数是否为3
        Assert.assertEquals(3, serverMessageList.size());

        ServerMessage serverMessage0 = serverMessageList.get(0);
        ServerMessage serverMessage1 = serverMessageList.get(1);
        ServerMessage serverMessage2 = serverMessageList.get(2);

        //Test:消息发送目标是否正确
        Assert.assertEquals(0, serverMessage0.getTargetId());
        Assert.assertEquals(20182906, serverMessage1.getTargetId());
        Assert.assertEquals(20173488, serverMessage2.getTargetId());

        //解析消息
        String message0 = serverMessage0.getMessage();
        String message1 = serverMessage1.getMessage();
        String message2 = serverMessage2.getMessage();
        String[] splitArray0 = message0.split("\\|", 2);
        String[] splitArray1 = message1.split("\\|", 2);
        String[] splitArray2 = message2.split("\\|", 2);

        //Test:消息头是否正确
        Assert.assertEquals("NewGroup", splitArray0[0]);
        Assert.assertEquals("NewGroup", splitArray1[0]);
        Assert.assertEquals("NewGroup", splitArray2[0]);

        //解析消息体
        String body0 = splitArray0[1];
        String body1 = splitArray1[1];
        String body2 = splitArray2[1];
        GroupInfoServerEntity groupInfoEntity0 = JSONObject.parseObject(body0, GroupInfoServerEntity.class);
        GroupInfoServerEntity groupInfoEntity1 = JSONObject.parseObject(body1, GroupInfoServerEntity.class);
        GroupInfoServerEntity groupInfoEntity2 = JSONObject.parseObject(body2, GroupInfoServerEntity.class);

        //Test:验证消息体内容是否正确
        Assert.assertEquals(1234, groupInfoEntity0.getGroupId());
        Assert.assertEquals(1234, groupInfoEntity1.getGroupId());
        Assert.assertEquals(1234, groupInfoEntity2.getGroupId());
        Assert.assertEquals("地平线小组", groupInfoEntity0.getGroupName());
        Assert.assertEquals("地平线小组", groupInfoEntity1.getGroupName());
        Assert.assertEquals("地平线小组", groupInfoEntity2.getGroupName());

        //Test:验证方法被执行情况
        verify(serviceDao).recordNewGroup(eq("地平线小组"), eq(20181575), anyString());
        verify(serviceDao, times(3)).recordNewMember(anyInt(), anyInt(), anyInt(), anyString());

    }

    @Test
    public void getGroupInfo() {

    }
}