package com.boemska.controllers;

import com.boemska.data.Winner;
import com.boemska.helpers.NumberGenerator;
import com.boemska.repos.WinnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class DrawController {
    @Autowired
    private WinnerRepository winnerRepository;
    private final SimpMessagingTemplate template;
    @Autowired
    DrawController(SimpMessagingTemplate template){
        this.template=template;
    }
    public int draw() {
        int ret = NumberGenerator.getInstance().generateSingle();
        if (NumberGenerator.getInstance().isCompleted()) {
            winnerRepository.save(new Winner(NumberGenerator.getInstance().getWinningCombination().getStringNumbers(), LocalDateTime.now()));
        }
        return ret;
    }
    @MessageMapping("/draw/start")
    public void start() throws InterruptedException {
        System.out.println("received message");
        for(int i=0;i<7;i++){
            this.template.convertAndSend("/draw",this.draw());
            Thread.sleep(1000);
        }
    }
}
