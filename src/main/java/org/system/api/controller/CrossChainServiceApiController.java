package org.system.api.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.system.api.biz.ApiBiz;
import org.system.api.dto.req.*;
import org.system.api.dto.resp.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/server/api")
public class CrossChainServiceApiController  {

    @Autowired
    private ApiBiz apiBiz;

    @PostMapping(value = "/applyDomainName")
    public DataResp<ApplyDomainNameRespDto> applyDomainName(@RequestHeader("accessToken") String accessToken, @Valid @RequestBody ApplyDomainNameReqDto applyDomainNameReqDto) {
        log.info("request url:{}******params:{}", "/server/api/applyDomainName", JSONObject.toJSON(applyDomainNameReqDto));
        return apiBiz.applyDomainName(accessToken, applyDomainNameReqDto);
    }

    @PostMapping(value = "/getDomainNameCert")
    public DataResp<GetDomainNameCertRespDto> getDomainNameCert(@RequestHeader("accessToken") String accessToken, @Valid @RequestBody GetDomainNameCertReqDto getDomainNameCertReqDto) {
        log.info("request url:{}******params:{}", "/server/api/getDomainNameCert", JSONObject.toJSON(getDomainNameCertReqDto));
        return apiBiz.getDomainNameCert(accessToken, getDomainNameCertReqDto);
    }

    @PostMapping(value = "/addBlockchainAnchor")
    public DataResp<AddBlockchainAnchorRespDto> addBlockchainAnchor(@RequestHeader("accessToken") String accessToken, @Valid @RequestBody AddBlockchainAnchorReqDto addBlockchainAnchorReqDto) {
        log.info("request url:{}******params:{}", "/server/api/addBlockchainAnchor", JSONObject.toJSON(addBlockchainAnchorReqDto));
        return apiBiz.addBlockchainAnchor(accessToken, addBlockchainAnchorReqDto);
    }

    @PostMapping(value = "/startBlockchainAnchor")
    public DataResp<StartBlockchainAnchorRespDto> startBlockchainAnchor(@RequestHeader("accessToken") String accessToken, @Valid @RequestBody StartBlockchainAnchorReqDto startBlockchainAnchorReqDto) {
        log.info("request url:{}******params:{}", "/server/api/startBlockchainAnchor", JSONObject.toJSON(startBlockchainAnchorReqDto));
        return apiBiz.startBlockchainAnchor(accessToken, startBlockchainAnchorReqDto);
    }

    @PostMapping(value = "/stopBlockchainAnchor")
    public DataResp<StopBlockchainAnchorRespDto> stopBlockchainAnchor(@RequestHeader("accessToken") String accessToken, @Valid @RequestBody StopBlockchainAnchorReqDto stopBlockchainAnchorReqDto) {
        log.info("request url:{}******params:{}", "/server/api/stopBlockchainAnchor", JSONObject.toJSON(stopBlockchainAnchorReqDto));
        return apiBiz.stopBlockchainAnchor(accessToken, stopBlockchainAnchorReqDto);
    }

    @PostMapping(value = "/getBlockchainContracts")
    public DataResp<GetBlockchainContractsRespDto> getBlockchainContracts(@RequestHeader("accessToken") String accessToken, @Valid @RequestBody GetBlockchainContractsReqDto getBlockchainContractsReqDto) {
        log.info("request url:{}******params:{}", "/server/api/getBlockchainContracts", JSONObject.toJSON(getBlockchainContractsReqDto));
        return apiBiz.getBlockchainContracts(accessToken, getBlockchainContractsReqDto);
    }

    @PostMapping(value = "/getBlockchainHeight")
    public DataResp<GetBlockchainHeightRespDto> getBlockchainHeight(@RequestHeader("accessToken") String accessToken, @Valid @RequestBody GetBlockchainHeightReqDto getBlockchainHeightReqDto) {
        log.info("request url:{}******params:{}", "/server/api/getBlockchainContracts", JSONObject.toJSON(getBlockchainHeightReqDto));
        return apiBiz.getBlockchainHeight(accessToken, getBlockchainHeightReqDto);
    }

    @PostMapping(value = "/getIpList")
    public DataResp<GetIpListRespDto> getIpList(@RequestHeader("accessToken") String accessToken) {
        log.info("request url:{}", "/server/api/getIpList");
        return apiBiz.getIpList(accessToken);
    }
}
