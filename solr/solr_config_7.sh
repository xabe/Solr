#!/bin/bash

SOLR_CORES=/opt/cores
SOLR_COLLECTIONS_HOME=/opt/solr/server/solr/mycores/

echo "[DEBUG] Processing " ${SOLR_CORES}

collections=$(find $SOLR_CORES -maxdepth 1 -mindepth 1 -type d)
for collection in $collections; do
    echo "[DEBUG] Processing collection " $(basename $collection)
    if [ ! -d $collection/conf ]; then
        echo "[DEBUG] Creating collection $(basename $collection) with default configuration"
        precreate-core $(basename $collection)
    else
        echo "[DEBUG] Creating collection $(basename $collection) with the specified configuration"
        SOLR_TMP_CORE=$SOLR_CORES/$(basename $collection)
        SOLR_COLLECTION_DIR=$SOLR_COLLECTIONS_HOME/$(basename $collection)

        mkdir -p $SOLR_COLLECTION_DIR
        cp -R $SOLR_TMP_CORE/conf $SOLR_COLLECTION_DIR/conf
        if [ -f $SOLR_TMP_CORE/core.properties ]; then
            cp $SOLR_TMP_CORE/core.properties $SOLR_COLLECTION_DIR/core.properties
        else
            touch $SOLR_COLLECTION_DIR/core.properties
        fi
    fi
done

exec solr -f &

until solr status; do
    echo "[DEBUG] Checking solr status..."
    sleep 5s
done

collections=$(find $SOLR_CORES -maxdepth 1 -mindepth 1 -type d)
for collection in $collections; do
    # Import existent documents
    if [ -d $collection/documents ]; then
        documents=$(find $collection/documents -maxdepth 1 -mindepth 1)
        echo "[DEBUG] - Importing all documents in collection $(basename $collection): $documents"
        post -c $(basename $collection) -recursive 1 $collection/documents
    fi
done

echo "[DEBUG] Creating /opt/solr/solr.lock"
mkdir /opt/solr/solr.lock

sleep 1000d