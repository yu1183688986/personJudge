package com.teacher.judge.demo.service.impl;

import com.teacher.judge.demo.bean.Configs;
import com.teacher.judge.demo.bo.Token;
import com.teacher.judge.demo.dao.TokenDao;
import com.teacher.judge.demo.enums.Constant;
import com.teacher.judge.demo.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

@Service
@Slf4j
@Transactional
public class TokenServiceImpl implements TokenService {
    @Autowired
    private TokenDao tokenDao;
    @Autowired
    private Configs configs;

    @Override
    public String insertToken(String userId) {
        Token token = new Token();
        token.setUserId(userId);
        token.setLoginDate(new Date());
        token.setValid(Constant.YES.getValue());
        token = tokenDao.save(token);
        log.info("插入的token={}", token.toString());
        return token.getTokenId();
    }

    @Override
    public boolean validToken(String tokenId) {
        Token tokenObj = tokenDao.getOne(tokenId);
        long currentTime = System.currentTimeMillis();
        log.info("token有效性=",tokenObj.getValid());
        log.info("当前时间={}", new Date());
        log.info("最大时间范围<{}", new Date(tokenObj.getLoginDate().getTime() + configs.getTimeOut() * 60 * 1000));
        if (tokenObj != null
                && Constant.YES.getValue().equals(tokenObj.getValid())
                && tokenObj.getLoginDate().getTime() + configs.getTimeOut() * 60 * 1000 > currentTime) {
            // token有效且时间没过期，则返回true
            return true;
        }
        return false;
    }

    @Override
    public void dropToken(String tokenId) {
        Token tokenObj = tokenDao.getOne(tokenId);
        tokenObj.setValid(Constant.NO.getValue());
        tokenDao.save(tokenObj);
    }
}
