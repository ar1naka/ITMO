package ru.itmo.wp.model.service;

import ru.itmo.wp.model.domain.Talk;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.ValidationException;
import ru.itmo.wp.model.repository.TalkRepository;
import ru.itmo.wp.model.repository.impl.TalkRepositoryImpl;
import com.google.common.base.Strings;

import java.util.List;


public class TalkService {
    private final TalkRepository talksService = new TalkRepositoryImpl();
    public void save(Talk talk) {
        talksService.save(talk);
    }

    public List<Talk> findAll() {
        return talksService.findAll();
    }

}
