package edu.bit.fishpond.service;

import com.alibaba.fastjson.JSON;
import edu.bit.fishpond.service.entity.GroupCreateClientEntity;
import edu.bit.fishpond.service.entity.GroupInfoServerEntity;
import edu.bit.fishpond.service.entity.UserIdEntity;
import edu.bit.fishpond.utils.DAOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("GroupService")
public class GroupService {

    @Autowired
    public GroupService(IServiceDao serviceDao){
        this.serviceDao = serviceDao;
    }

    private final IServiceDao serviceDao;
    private final Logger logger = LoggerFactory.getLogger(GroupService.class);

    public ServiceResult groupCreate(GroupCreateClientEntity entity) throws DAOException {
        ServiceResult result = new ServiceResult();
        Map<Integer, String> map = new HashMap<>();


        int creatorId = entity.getCreatorId();
        String groupName = entity.getGroupName();
        List<Integer> initialMembers = entity.getInitialMembers();

        LocalDateTime currentTime = LocalDateTime.now();
        String currentTimeString = currentTime.toString();

        //获取新建的群聊的ID
        int groupId = serviceDao.recordNewGroup(groupName, creatorId, currentTimeString);
        for (int memberId: initialMembers) {
            // 将初始的每个群成员记录
            serviceDao.recordNewMember(groupId, memberId, creatorId, currentTimeString);
            // 查询每个初始成员的在线状态
            boolean onlineStatus = serviceDao.queryOnlineStatusById(memberId);
            //如果该成员在线，向该成员发送信息
            if (onlineStatus){
                List<GroupInfoServerEntity> resultList = new ArrayList<>();
                List<String> queryResult = serviceDao.queryGroupByUserId(memberId);

                if (!queryResult.isEmpty()){
                    for (String queryLine : queryResult){
                        String[] queryDataArray = queryLine.split("#");
                        if (queryDataArray.length == 2){
                            GroupInfoServerEntity groupInfoServerEntity = new GroupInfoServerEntity();
                            groupInfoServerEntity.setGroupId(Integer.parseInt(queryDataArray[0]));
                            groupInfoServerEntity.setGroupName(queryDataArray[1]);
                            resultList.add(groupInfoServerEntity);
                        }
                        else {
                            logger.error(String.format("无法解析数据层数据:%s,解析后实际length为:%d，设定为2",
                                    queryLine,queryDataArray.length));
                        }
                    }
                }

                String sendMessageBody = JSON.toJSONString(resultList);
                map.put(memberId, "AllGroup|" + sendMessageBody);

            }
        }

        result.setSendMessage(true);
        result.setSenderMessageMap(map);
        return result;
    }

    public ServiceResult getGroupList(UserIdEntity entity){
        ServiceResult result = new ServiceResult();
        Map<Integer, String> map = new HashMap<>();

        List<GroupInfoServerEntity> resultList = new ArrayList<>();

        int id = entity.getUserId();
        List<String> queryResult = serviceDao.queryGroupByUserId(id);

        if (!queryResult.isEmpty()){
            for (String queryLine : queryResult){
                String[] queryDataArray = queryLine.split("#");
                if (queryDataArray.length == 2){
                    GroupInfoServerEntity groupInfoServerEntity = new GroupInfoServerEntity();
                    groupInfoServerEntity.setGroupId(Integer.parseInt(queryDataArray[0]));
                    groupInfoServerEntity.setGroupName(queryDataArray[1]);
                    resultList.add(groupInfoServerEntity);
                }
                else {
                    logger.error(String.format("无法解析数据层数据:%s,解析后实际length为:%d，设定为2",
                            queryLine,queryDataArray.length));
                }
            }
        }

        String sendMessageBody = JSON.toJSONString(resultList);
        map.put(id, "GroupList|" + sendMessageBody);
        result.setSendMessage(true);
        result.setSenderMessageMap(map);

        return result;
    }

    public List<ServerMessage> getGroupListHandler(UserIdEntity entity){
        List<ServerMessage> serverMessageList = new ArrayList<>();

        List<GroupInfoServerEntity> resultList = new ArrayList<>();

        int id = entity.getUserId();
        List<String> queryResult = serviceDao.queryGroupByUserId(id);

        if (!queryResult.isEmpty()){
            for (String queryLine : queryResult){
                String[] queryDataArray = queryLine.split("#");
                if (queryDataArray.length == 2){
                    GroupInfoServerEntity groupInfoServerEntity = new GroupInfoServerEntity();
                    groupInfoServerEntity.setGroupId(Integer.parseInt(queryDataArray[0]));
                    groupInfoServerEntity.setGroupName(queryDataArray[1]);
                    resultList.add(groupInfoServerEntity);
                }
                else {
                    logger.error(String.format("无法解析数据层数据:%s,解析后实际length为:%d，设定为2",
                            queryLine,queryDataArray.length));
                }
            }
        }

        String sendMessageBody = JSON.toJSONString(resultList);
        serverMessageList.add(new ServerMessage(0, "GroupList", sendMessageBody));

        return serverMessageList;
    }
}
