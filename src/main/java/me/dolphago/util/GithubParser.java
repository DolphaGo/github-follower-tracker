package me.dolphago.util;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import me.dolphago.domain.MyData;

@Service
@Slf4j
public class GithubParser {
    ObjectMapper objectMapper;

    @PostConstruct
    public void createMapper() {
        objectMapper = new ObjectMapper();
    }

    public List<MyData> followerParse(Object o) throws JsonProcessingException {
        List<MyData> datas = new ArrayList<>();
        JsonNode jsonNode = objectMapper.readTree(o.toString());
        log.info("확인 ------------ : {}",jsonNode);
        return datas;
    }
}
