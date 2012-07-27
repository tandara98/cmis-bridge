package com.backbase.expert.cmis.ptc;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.backbase.portal.ptc.cache.CacheKey;
import com.backbase.portal.ptc.context.MutableProxyContext;
import com.backbase.portal.ptc.request.MutableProxyRequest;
import com.backbase.portal.ptc.transform.request.AbstractRequestTransformer;

public class ProxyParamsCacheKeyRequestTransformer extends AbstractRequestTransformer {

	private static final Logger log = LoggerFactory.getLogger(ProxyParamsCacheKeyRequestTransformer.class);

	public void transform(MutableProxyContext mutableProxyContext, MutableProxyRequest mutableProxyRequest) {

		HttpServletRequest request = mutableProxyRequest.getRequest();

		@SuppressWarnings("unchecked")
		Map<String, String[]> parameters = request.getParameterMap();
		Set<String> keys = parameters.keySet();

		if (log.isDebugEnabled()) {
			log.debug("GET params({}):", keys.size());
			for (String key : keys) {
				String[] value = parameters.get(key);
				log.debug("\tCopy key={},value={} to CacheKey GET parameter", key, StringUtils.join(value));
			}
		}

		CacheKey cacheKey = mutableProxyContext.getCacheKey();
		cacheKey.setGetParameters(parameters);
	}
}