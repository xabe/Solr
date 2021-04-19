FROM solr:8.8.2

USER root

ENV VERBOSE=true

ADD data /opt/cores
ADD solr_config.sh /opt/docker-solr/scripts

EXPOSE 8983/tcp

WORKDIR /opt/solr

RUN chown -R solr:solr /opt/docker-solr/scripts/solr_config.sh 
RUN chmod +x /opt/docker-solr/scripts/solr_config.sh
USER solr

ENTRYPOINT ["/opt/docker-solr/scripts/solr_config.sh"]