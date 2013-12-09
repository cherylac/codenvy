/*
 *
 * CODENVY CONFIDENTIAL
 * ________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 * NOTICE: All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any. The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */

IMPORT 'macros.pig';

l = loadResources('$LOG', '$FROM_DATE', '$TO_DATE', '$USER', '$WS');

a1 = filterByEvent(l, 'factory-url-accepted');
a2 = extractUrlParam(a1, 'REFERRER', 'referrer');
a3 = extractUrlParam(a2, 'FACTORY-URL', 'factoryUrl');
a4 = extractUrlParam(a3, 'ORG-ID', 'orgId');
a5 = extractUrlParam(a4, 'AFFILIATE-ID', 'affiliateId');
a = FOREACH a5 GENERATE dt, ws, user, referrer, factoryUrl, orgId, affiliateId;

result = FOREACH a GENERATE ToMilliSeconds(dt), TOTUPLE('value', factoryUrl);
STORE result INTO '$STORAGE_URL.$STORAGE_TABLE' USING MongoStorage('$STORAGE_USER', '$STORAGE_PASSWORD');

r1 = FOREACH a GENERATE dt, ws, user, LOWER(REGEX_EXTRACT(user, '.*@(.*)', 1)) AS domain, factoryUrl, referrer, orgId, affiliateId;
r = FOREACH r1 GENERATE ToMilliSeconds(dt), TOTUPLE('ws', ws), TOTUPLE('user', user), TOTUPLE('domain', domain),
                    TOTUPLE('org_id', orgId), TOTUPLE('affiliate_id', affiliateId), TOTUPLE('referrer', referrer),
                    TOTUPLE('value', factoryUrl);
STORE r INTO '$STORAGE_URL.$STORAGE_TABLE-raw' USING MongoStorage('$STORAGE_USER', '$STORAGE_PASSWORD');
