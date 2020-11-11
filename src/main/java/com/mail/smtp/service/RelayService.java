package com.mail.smtp.service;

import com.mail.smtp.entity.DomainEntity;
import com.mail.smtp.entity.RelayEntity;
import com.mail.smtp.repository.RelayRepository;
import com.mail.smtp.util.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RelayService
{
    private final RelayRepository relayRepository;

    public boolean relayAllowed(Integer domainIndex, String connectionIp)
    {
        DomainEntity domainEntity = new DomainEntity();
        domainEntity.setIdx(domainIndex);

        Optional< List< RelayEntity > > optRelayList = relayRepository.findByDomain(domainEntity);
        List<RelayEntity> relayList = optRelayList.orElseGet(Collections::emptyList);

        return relayList.stream().anyMatch((relay) -> {
            if( relay.getType() == 0 )
                return relay.getIp().equals(connectionIp);
            else
                return CommonUtil.isSubnetRange(relay.getIp(), connectionIp);
        });
    }
}
