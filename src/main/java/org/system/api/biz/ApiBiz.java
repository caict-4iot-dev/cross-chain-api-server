package org.system.api.biz;

import cn.ac.caict.bid.model.BIDDocumentOperation;
import cn.ac.caict.bid.model.BIDpublicKeyOperation;
import cn.bif.common.JsonUtils;
import cn.bif.module.encryption.key.PrivateKeyManager;
import cn.bif.module.encryption.key.PublicKeyManager;
import cn.bif.module.encryption.model.KeyType;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alipay.antchain.bridge.commons.bcdns.utils.CrossChainCertificateUtil;
import com.alipay.antchain.bridge.relayer.facade.admin.GRpcRelayerAdminClient;
import com.alipay.antchain.bridge.relayer.facade.admin.IRelayerAdminClient;
import com.alipay.antchain.bridge.relayer.facade.admin.types.CrossChainMsgACLItem;
import com.alipay.antchain.bridge.relayer.facade.admin.types.SysContractsInfo;
import com.alipay.antchain.bridge.relayer.facade.admin.utils.FacadeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.system.api.common.constant.Constants;
import org.system.api.common.utils.AppUtils;
import org.system.api.common.utils.JwtUtil;
import org.system.api.common.utils.RedisUtil;
import org.system.api.common.utils.Tools;
import org.system.api.dto.req.*;
import org.system.api.dto.resp.*;
import org.system.api.enums.ExceptionEnum;
import org.system.api.exception.APIException;
import org.system.api.model.ApiKeyDomain;
import org.system.api.service.ApiKeyService;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;


@Component
public class ApiBiz {
    private IRelayerAdminClient relayAdminClient;

    @Value("${relay.admin.address}")
    private String relayAdminAddress;

    @Value("${object-identity.manager.address}")
    private String managerAddress;

    @Value("${bif.ipList}")
    private String ipList;

    @PostConstruct
    public void init() {
        relayAdminClient = new GRpcRelayerAdminClient(relayAdminAddress);
    }

    private static final Logger logger = LoggerFactory.getLogger(ApiBiz.class);

    @Autowired
    private RedisUtil redisUtil;

    private static final String pluginServerId = "bifPS.id";

    public DataResp<ApplyDomainNameRespDto> applyDomainName(String accessToken, ApplyDomainNameReqDto applyDomainNameReqDto) {
        DataResp<ApplyDomainNameRespDto> dataResp = new DataResp<ApplyDomainNameRespDto>();
        try {
            //check access token
            Map<String, String> paramMap = JwtUtil.decode(accessToken);
            if (paramMap == null) {
                throw new APIException(ExceptionEnum.ACCESS_TOKEN_INVALID);
            }

            String userId = paramMap.get(Constants.USER_ID);
            String token = redisUtil.get(userId);
            if (!token.equals(accessToken)) {
                throw new APIException(ExceptionEnum.ACCESS_TOKEN_INVALID);
            }

            if (applyDomainNameReqDto.getPublicKey().length() != 70) {
                throw new APIException(ExceptionEnum.PARAM_ERROR.getErrorCode(), "the length of the public key is 70");
            }
            if (!PublicKeyManager.isPublicKeyValid(applyDomainNameReqDto.getPublicKey())) {
                throw new APIException(ExceptionEnum.PARAM_ERROR.getErrorCode(), "invalid public key");
            }

            BIDpublicKeyOperation[] biDpublicKeyOperation = new BIDpublicKeyOperation[1];
            biDpublicKeyOperation[0] = new BIDpublicKeyOperation();
            biDpublicKeyOperation[0].setType(KeyType.ED25519);
            biDpublicKeyOperation[0].setPublicKeyHex(applyDomainNameReqDto.getPublicKey());
            BIDDocumentOperation bidDocumentOperation = new BIDDocumentOperation();
            bidDocumentOperation.setPublicKey(biDpublicKeyOperation);

            String applyNo = relayAdminClient.applyDomainNameCert("", applyDomainNameReqDto.getDomainName().trim(), bidDocumentOperation);
            ApplyDomainNameRespDto applyDomainNameRespDto = new ApplyDomainNameRespDto();
            applyDomainNameRespDto.setApplyNo(applyNo);
            dataResp.setData(applyDomainNameRespDto);
            dataResp.buildSuccessField();
        } catch (APIException e) {
            dataResp.buildAPIExceptionField(e);
        } catch (IllegalArgumentException e) {
            dataResp.buildArgumentExceptionField(e);
        } catch (FacadeException e) {
            dataResp.buildFacadeExceptionField(e);
        } catch (Exception e) {
            logger.error("apply domain name error");
            dataResp.buildSysExceptionField();
        }
        return dataResp;
    }

    public DataResp<GetDomainNameCertRespDto> getDomainNameCert(String accessToken, GetDomainNameCertReqDto getDomainNameCertReqDto) {
        DataResp<GetDomainNameCertRespDto> dataResp = new DataResp<GetDomainNameCertRespDto>();
        try {
            //check access token
            Map<String, String> paramMap = JwtUtil.decode(accessToken);
            if (paramMap == null) {
                throw new APIException(ExceptionEnum.ACCESS_TOKEN_INVALID);
            }

            String userId = paramMap.get(Constants.USER_ID);
            String token = redisUtil.get(userId);
            if (!token.equals(accessToken)) {
                throw new APIException(ExceptionEnum.ACCESS_TOKEN_INVALID);
            }

            String cert = relayAdminClient.queryDomainNameCertFromBCDNS(getDomainNameCertReqDto.getDomainName().trim(),"").encodeToBase64();
            GetDomainNameCertRespDto getDomainNameCertRespDto = new GetDomainNameCertRespDto();
            getDomainNameCertRespDto.setCredential(cert);
            dataResp.setData(getDomainNameCertRespDto);
            dataResp.buildSuccessField();
        } catch (APIException e) {
            dataResp.buildAPIExceptionField(e);
        } catch (IllegalArgumentException e) {
            dataResp.buildArgumentExceptionField(e);
        } catch (FacadeException e) {
            dataResp.buildFacadeExceptionField(e);
        } catch (Exception e) {
            logger.error("get domain name cert error");
            dataResp.buildSysExceptionField();
        }
        return dataResp;
    }

    public DataResp<AddBlockchainAnchorRespDto> addBlockchainAnchor(String accessToken, AddBlockchainAnchorReqDto addBlockchainAnchorReqDto) {
        DataResp<AddBlockchainAnchorRespDto> dataResp = new DataResp<AddBlockchainAnchorRespDto>();
        try {
            //check access token
            Map<String, String> paramMap = JwtUtil.decode(accessToken);
            if (paramMap == null) {
                throw new APIException(ExceptionEnum.ACCESS_TOKEN_INVALID);
            }

            String userId = paramMap.get(Constants.USER_ID);
            String token = redisUtil.get(userId);
            if (!token.equals(accessToken)) {
                throw new APIException(ExceptionEnum.ACCESS_TOKEN_INVALID);
            }

            relayAdminClient.addBlockchainAnchor(addBlockchainAnchorReqDto.getProduct(), AppUtils.getAppId(), addBlockchainAnchorReqDto.getDomainName(), pluginServerId, addBlockchainAnchorReqDto.getConfFile().getBytes());
            AddBlockchainAnchorRespDto addBlockchainAnchorRespDto = new AddBlockchainAnchorRespDto();
            addBlockchainAnchorRespDto.setResult(true);
            dataResp.setData(addBlockchainAnchorRespDto);
            dataResp.buildSuccessField();
        } catch (APIException e) {
            dataResp.buildAPIExceptionField(e);
        } catch (IllegalArgumentException e) {
            dataResp.buildArgumentExceptionField(e);
        } catch (FacadeException e) {
            dataResp.buildFacadeExceptionField(e);
        } catch (Exception e) {
            logger.error("add blockchain anchor error");
            dataResp.buildSysExceptionField();
        }
        return dataResp;
    }

    public DataResp<StartBlockchainAnchorRespDto> startBlockchainAnchor(String accessToken, StartBlockchainAnchorReqDto startBlockchainAnchorReqDto) {
        DataResp<StartBlockchainAnchorRespDto> dataResp = new DataResp<StartBlockchainAnchorRespDto>();
        try {
            //check access token
            Map<String, String> paramMap = JwtUtil.decode(accessToken);
            if (paramMap == null) {
                throw new APIException(ExceptionEnum.ACCESS_TOKEN_INVALID);
            }

            String userId = paramMap.get(Constants.USER_ID);
            if (!userId.equals(managerAddress)) {
                throw new APIException(ExceptionEnum.NOT_API_MANAGER);
            }
            String token = redisUtil.get(userId);
            if (!token.equals(accessToken)) {
                throw new APIException(ExceptionEnum.ACCESS_TOKEN_INVALID);
            }

            relayAdminClient.startBlockchainAnchor(startBlockchainAnchorReqDto.getDomainName().trim());
            StartBlockchainAnchorRespDto startBlockchainAnchorRespDto = new StartBlockchainAnchorRespDto();
            startBlockchainAnchorRespDto.setResult(true);
            dataResp.setData(startBlockchainAnchorRespDto);
            dataResp.buildSuccessField();
        } catch (APIException e) {
            dataResp.buildAPIExceptionField(e);
        } catch (IllegalArgumentException e) {
            dataResp.buildArgumentExceptionField(e);
        } catch (FacadeException e) {
            dataResp.buildFacadeExceptionField(e);
        } catch (Exception e) {
            logger.error("start blockchain anchor error");
            dataResp.buildSysExceptionField();
        }
        return dataResp;
    }

    public DataResp<StopBlockchainAnchorRespDto> stopBlockchainAnchor(String accessToken, StopBlockchainAnchorReqDto stopBlockchainAnchorReqDto) {
        DataResp<StopBlockchainAnchorRespDto> dataResp = new DataResp<StopBlockchainAnchorRespDto>();
        try {
            //check access token
            Map<String, String> paramMap = JwtUtil.decode(accessToken);
            if (paramMap == null) {
                throw new APIException(ExceptionEnum.ACCESS_TOKEN_INVALID);
            }

            String userId = paramMap.get(Constants.USER_ID);
            if (!userId.equals(managerAddress)) {
                throw new APIException(ExceptionEnum.NOT_API_MANAGER);
            }
            String token = redisUtil.get(userId);
            if (!token.equals(accessToken)) {
                throw new APIException(ExceptionEnum.ACCESS_TOKEN_INVALID);
            }

            relayAdminClient.stopBlockchainAnchor(stopBlockchainAnchorReqDto.getDomainName().trim());
            StopBlockchainAnchorRespDto stopBlockchainAnchorRespDto = new StopBlockchainAnchorRespDto();
            stopBlockchainAnchorRespDto.setResult(true);
            dataResp.setData(stopBlockchainAnchorRespDto);
            dataResp.buildSuccessField();
        } catch (APIException e) {
            dataResp.buildAPIExceptionField(e);
        } catch (IllegalArgumentException e) {
            dataResp.buildArgumentExceptionField(e);
        } catch (FacadeException e) {
            dataResp.buildFacadeExceptionField(e);
        } catch (Exception e) {
            logger.error("stop blockchain anchor error");
            dataResp.buildSysExceptionField();
        }
        return dataResp;
    }

    public DataResp<GetBlockchainContractsRespDto> getBlockchainContracts(String accessToken, GetBlockchainContractsReqDto getBlockchainContractsReqDto) {
        DataResp<GetBlockchainContractsRespDto> dataResp = new DataResp<GetBlockchainContractsRespDto>();
        try {
            //check access token
            Map<String, String> paramMap = JwtUtil.decode(accessToken);
            if (paramMap == null) {
                throw new APIException(ExceptionEnum.ACCESS_TOKEN_INVALID);
            }

            String userId = paramMap.get(Constants.USER_ID);
            String token = redisUtil.get(userId);
            if (!token.equals(accessToken)) {
                throw new APIException(ExceptionEnum.ACCESS_TOKEN_INVALID);
            }

            SysContractsInfo sysContractsInfo = relayAdminClient.getBlockchainContracts(getBlockchainContractsReqDto.getDomainName());
            GetBlockchainContractsRespDto getBlockchainContractsRespDto = new GetBlockchainContractsRespDto();
            getBlockchainContractsRespDto.setInforJson(JsonUtils.toJSONString(sysContractsInfo));
            dataResp.setData(getBlockchainContractsRespDto);
            dataResp.buildSuccessField();
        } catch (APIException e) {
            dataResp.buildAPIExceptionField(e);
        } catch (IllegalArgumentException e) {
            dataResp.buildArgumentExceptionField(e);
        } catch (FacadeException e) {
            dataResp.buildFacadeExceptionField(e);
        } catch (Exception e) {
            logger.error("get blockchain contracts error");
            dataResp.buildSysExceptionField();
        }
        return dataResp;
    }

    public DataResp<GetBlockchainHeightRespDto> getBlockchainHeight(String accessToken, GetBlockchainHeightReqDto getBlockchainHeightReqDto) {
        DataResp<GetBlockchainHeightRespDto> dataResp = new DataResp<GetBlockchainHeightRespDto>();
        try {
            //check access token
            Map<String, String> paramMap = JwtUtil.decode(accessToken);
            if (paramMap == null) {
                throw new APIException(ExceptionEnum.ACCESS_TOKEN_INVALID);
            }

            String userId = paramMap.get(Constants.USER_ID);
            String token = redisUtil.get(userId);
            if (!token.equals(accessToken)) {
                throw new APIException(ExceptionEnum.ACCESS_TOKEN_INVALID);
            }

            String heightStr = relayAdminClient.getBlockchainHeights(getBlockchainHeightReqDto.getDomainName());
            String cleanedStr = heightStr.replaceAll("[\n\t]+", "");
            GetBlockchainHeightRespDto getBlockchainHeightRespDto = new GetBlockchainHeightRespDto();
            getBlockchainHeightRespDto.setHeight(cleanedStr);
            dataResp.setData(getBlockchainHeightRespDto);
            dataResp.buildSuccessField();
        } catch (APIException e) {
            dataResp.buildAPIExceptionField(e);
        } catch (IllegalArgumentException e) {
            dataResp.buildArgumentExceptionField(e);
        } catch (FacadeException e) {
            dataResp.buildFacadeExceptionField(e);
        } catch (Exception e) {
            logger.error("get blockchain height error");
            dataResp.buildSysExceptionField();
        }
        return dataResp;
    }

    public DataResp<GetIpListRespDto> getIpList(String accessToken) {
        DataResp<GetIpListRespDto> dataResp = new DataResp<GetIpListRespDto>();
        try {
            //check access token
            Map<String, String> paramMap = JwtUtil.decode(accessToken);
            if (paramMap == null) {
                throw new APIException(ExceptionEnum.ACCESS_TOKEN_INVALID);
            }

            String userId = paramMap.get(Constants.USER_ID);
            String token = redisUtil.get(userId);
            if (!token.equals(accessToken)) {
                throw new APIException(ExceptionEnum.ACCESS_TOKEN_INVALID);
            }

            GetIpListRespDto getIpListRespDto = new GetIpListRespDto();
            getIpListRespDto.setInforJson(ipList);
            dataResp.setData(getIpListRespDto);
            dataResp.buildSuccessField();
        } catch (APIException e) {
            dataResp.buildAPIExceptionField(e);
        } catch (IllegalArgumentException e) {
            dataResp.buildArgumentExceptionField(e);
        } catch (Exception e) {
            logger.error("get ip list error");
            dataResp.buildSysExceptionField();
        }
        return dataResp;
    }
}

