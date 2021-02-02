## Spring Boot y Solr

En este ejemplo vemos como usar spring boot con solr como base de datos:

- Spring boot
- Grpc
- Solr

Lo primero que tenemos que hacer es crear el schema en solr de **vehicles** para guardar la información de los
vehículos.

Con este comando vamos a crear la imagen con el script de bash que crear el schema y hace una carga inicial de datos.

```shell script
docker-compose build
docker-compoer up -d
```

Una vez levantado nuestro solr para comprobar que se ha _levantado_ correctamente podemos acceder a la consola de solr
con la siguiente url

```shell script
open http://localhost:8983/solr/#/vehicles/query
```

También podemos llamar Api Solr para obtener todos los documentos de solr

```shell script
curl -X GET http://localhost:8983/solr/vehicles/select?q=*%3A*
```

Una vez que tenemos nuestro Solr lo siente es arranca nuestra aplicación con el siguiente comando

```shell script
mvn clean install

cd solr-boot/

mvn spring-boot:run -Dstart-class=com.xabe.spring.solr.App -Dspring-boot.run.profiles=standalone
```

Ahora vemos a ver los servicios que tenemos disponible con el siguiente de la herramienta

```shell script
grpcurl --plaintext localhost:6565 list
com.xabe.vehicle.search.SearchVehicleService
com.xabe.vehicle.update.UpdateVehicleService
grpc.health.v1.Health
grpc.reflection.v1alpha.ServerReflection
```

Lo primero que tenemos que hacer is alimentar nuestro solr con el servicio de **UpdateVehicleService**

```shell script
grpcurl --plaintext localhost:6565 describe com.xabe.vehicle.update.UpdateVehicleService
com.xabe.vehicle.update.UpdateVehicleService is a service:
service UpdateVehicleService {
rpc UpdateCarInfo ( .com.xabe.vehicle.update.UpdateCarInfoRequest ) returns ( .com.xabe.vehicle.update.UpdateCarInfoResponse );
rpc UpdateTruckInfo ( .com.xabe.vehicle.update.UpdateTruckInfoRequest ) returns ( .com.xabe.vehicle.update.UpdateTruckInfoResponse );
rpc UpdateVehicleBasicInfo ( .com.xabe.vehicle.update.UpdateVehicleBasicRequest ) returns ( .com.xabe.vehicle.update.UpdateVehicleBasicResponse );
rpc UpdateVehicleColor ( .com.xabe.vehicle.update.UpdateVehicleColorRequest ) returns ( .com.xabe.vehicle.update.UpdateVehicleColorResponse );
rpc UpdateVehicleEngine ( .com.xabe.vehicle.update.`UpdateVehicleEngineRequest ) returns ( .com.xabe.vehicle.update.UpdateVehicleEngineResponse );
rpc UpdateVehiclePrice ( .com.xabe.vehicle.update.UpdateVehiclePriceRequest ) returns ( .com.xabe.vehicle.update.UpdateVehiclePriceResponse );
rpc UpdateVehicleTransmission ( .com.xabe.vehicle.update.UpdateVehicleTransmissionRequest ) returns ( .com.xabe.vehicle.update.UpdateVehicleTransmissionResponse );
rpc UpdateVehicleWheel ( .com.xabe.vehicle.update.UpdateVehicleWheelRequest ) returns ( .com.xabe.vehicle.update.UpdateVehicleWheelResponse );
}
```

Lo siguiente es obtener los campos que forma la request de cada petición.

```shell script
grpcurl --plaintext localhost:6565 describe .com.xabe.vehicle.update.UpdateVehicleBasicRequest
com.xabe.vehicle.update.UpdateVehicleBasicRequest is a message:
message UpdateVehicleBasicRequest {
  .com.xabe.vehicle.VehicleId id = 1;
  int64 timestamp = 2;
  string store_id = 3;
  string category = 4;
  string brand = 5;
  string model = 6;
}
```

Una vez con la descripción de la request realizamos la llamada.

```shell script
grpcurl -d @ --plaintext localhost:6565 com.xabe.vehicle.update.UpdateVehicleService/UpdateVehicleBasicInfo <<EOM 
{ 
"id" : { "id":"MAZDA" , "type":"VEHICLE_CAR"},
"timestamp": 1609141000,
"store_id":"SP",
"category":"Sedan, Hatchback",
"brand":"MAZDA",
"model":"MAZDA3"
} 
EOM
``

```shell script
grpcurl -d @ --plaintext localhost:6565 com.xabe.vehicle.update.UpdateVehicleService/UpdateVehicleColor <<EOM 
{ 
"id" : { "id":"MAZDA" , "type":"VEHICLE_CAR"},
"timestamp": 1609141000,
"store_id":"SP",
"colors":[
  {
     "id":"1",
     "type": "COLOR_SOLID",
     "color_names": [
        {
            "text": "rojo",
            "locale": "es-ES"
        },
        {
            "text": "red",
            "locale": "en-GB"
        }
     ]
  },
  {
     "id":"2",
     "type": "COLOR_SOLID",
     "color_names": [
        {
            "text": "amarillo",
            "locale": "es-ES"
        },
        {
            "text": "yellow",
            "locale": "en-GB"
        }
     ]
  }
]
} 
EOM
```

```shell script
grpcurl -d @ --plaintext localhost:6565 com.xabe.vehicle.update.UpdateVehicleService/UpdateVehicleEngine <<EOM 
{ 
"id" : { "id":"MAZDA" , "type":"VEHICLE_CAR"},
"timestamp": 1609141000,
"store_id":"SP",
"engines":[
    {
    "type": "ENGINE_IN_LINE",
    "fuel": "FUEL_PETROL"
    },
    {
    "type": "ENGINE_IN_LINE",
    "fuel": "FUEL_ELECTRIC"
    }
]
} 
EOM
```

```shell script
grpcurl -d @ --plaintext localhost:6565 com.xabe.vehicle.update.UpdateVehicleService/UpdateVehiclePrice <<EOM 
{ 
"id" : { "id":"MAZDA" , "type":"VEHICLE_CAR"},
"timestamp": 1609141000,
"store_id":"SP",
"prices": {
  "currency_code": "EUR",
  "prices": [
      {
         "price":  24000,
         "discount": 0
      },
      {
         "price":  20000,
         "discount": 0
      }
  ]
}
} 
EOM
```

```shell script
grpcurl -d @ --plaintext localhost:6565 com.xabe.vehicle.update.UpdateVehicleService/UpdateVehicleTransmission <<EOM 
{ 
"id" : { "id":"MAZDA" , "type":"VEHICLE_CAR"},
"timestamp": 1609141000,
"store_id":"SP",
"transmissions": [
  "TRANSMISSION_AUTOMATIC",
  "TRANSMISSION_MANUAL"
]
} 
EOM
```

```shell script
grpcurl -d @ --plaintext localhost:6565 com.xabe.vehicle.update.UpdateVehicleService/UpdateVehicleWheel <<EOM 
{ 
"id" : { "id":"MAZDA" , "type":"VEHICLE_CAR"},
"timestamp": 1609141000,
"store_id":"SP",
"wheels": [
  {
    "count": 4,
    "size": 18,
    "type": "WHEEL_ALL_SEASONS"
  },
  {
    "count": 4,
    "size": 16,
    "type": "WHEEL_SUMMER"
  }
]
} 
EOM
```

```shell script
grpcurl -d @ --plaintext localhost:6565 com.xabe.vehicle.update.UpdateVehicleService/UpdateCarInfo <<EOM 
{ 
"id" : { "id":"MAZDA" , "type":"VEHICLE_CAR"},
"timestamp": 1609141000,
"store_id":"SP",
"doors": 5,
"type": "CAR_HATCHBACK"
} 
EOM
```

Una vez creado un vehículo lo siguiente es hacer una búsqueda

Lo primero que tenemos que hacer is alimentar nuestro solr con el servicio de **UpdateVehicleService**

```shell script
 grpcurl --plaintext localhost:6565 describe com.xabe.vehicle.search.SearchVehicleService
com.xabe.vehicle.search.SearchVehicleService is a service:
service SearchVehicleService {
  rpc GetVehicle ( .com.xabe.vehicle.search.GetVehicleRequest ) returns ( .com.xabe.vehicle.search.GetVehicleResponse );
}
```

Invocamos una búsqueda

```shell script
grpcurl -d @ --plaintext localhost:6565 com.xabe.vehicle.search.SearchVehicleService/GetVehicle <<EOM 
{ 
"filter" : "sedan",
"visibility_version": 9999999999999,
"store_id":  "SP",
"locale": "es-ES",
"filter_has_prices": true,
"sections":[
 {
   "sequence_no": 0,
   "filter_query": "HATCHBACK"
 }
]
} 
EOM
```
