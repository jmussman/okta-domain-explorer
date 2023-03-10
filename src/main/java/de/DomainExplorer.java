// DomainExplorer.java
// Copyright © 2023 Joel Mussman. All rights reserved.
// This code is release under the MIT license.
//

package de;

import com.okta.sdk.client.Clients;
import org.openapitools.client.ApiClient;
import org.openapitools.client.api.DomainApi;
import org.openapitools.client.model.DomainCertificateMetadata;
import org.openapitools.client.model.DomainCertificateSourceType;
import org.openapitools.client.model.DomainListResponse;
import org.openapitools.client.model.DomainResponse;

public class DomainExplorer {

    public static void main(String args[]) {

        // The builder will read OKTA_CLIENT_ORGURL and OKTA_CLIENT_TOKEN from the environment; look at the
        // documentation: https://github.com/okta/okta-sdk-java/blob/master/README.md#configuration-reference.

        ApiClient client = Clients.builder().build();
        DomainApi domainApi = new DomainApi(client);
        DomainListResponse domains = domainApi.listDomains();

        for (DomainResponse domain : domains.getDomains()) {

            // The list of domains contains truncated DomainResponse objects, retrieve each domain
            // to get the certificate.

            String name = domain.getDomain();
            String domainId = domain.getId();
            DomainResponse fullDomain = domainApi.getDomain(domainId);

            // Drill down in the certificate to the expiration.

            DomainCertificateSourceType domainCertificateSourceType = fullDomain.getCertificateSourceType();
            String sourceType = domainCertificateSourceType == null ? null : domainCertificateSourceType.getValue();
            DomainCertificateMetadata domainCertificateMetadata = fullDomain.getPublicCertificate();
            String expires = "unknown";

            if (domainCertificateMetadata != null) {

                expires = domainCertificateMetadata.getExpiration();
            }

            // Format the record and print.

            System.out.println(String.format("%s: certificate expires %s%s", name, expires, "MANUAL".equals(sourceType) ? "" : " (okta managed)"));
        }
    }

}
