package com.xabe.spring.solr.acceptance;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.core.type.TypeReference;
import com.xabe.spring.solr.App;
import com.xabe.spring.solr.acceptance.data.Car;
import com.xabe.spring.solr.acceptance.data.CarResponse;
import com.xabe.spring.solr.acceptance.data.CountResponse;
import com.xabe.spring.solr.acceptance.util.GrpcUtil;
import com.xabe.spring.solr.acceptance.util.JsonBodyHandlers;
import com.xabe.spring.solr.acceptance.util.Pagination;
import com.xabe.spring.solr.acceptance.util.PaginationIterator;
import com.xabe.vehicle.api.grpc.CarTypeOuterClass.CarType;
import com.xabe.vehicle.api.grpc.ColorTypeOuterClass.ColorType;
import com.xabe.vehicle.api.grpc.EngineOuterClass.Engine;
import com.xabe.vehicle.api.grpc.EngineTypeOuterClass.EngineType;
import com.xabe.vehicle.api.grpc.FuelTypeOuterClass.FuelType;
import com.xabe.vehicle.api.grpc.TransmissionTypeOuterClass.TransmissionType;
import com.xabe.vehicle.api.grpc.VehicleIdOuterClass.VehicleId;
import com.xabe.vehicle.api.grpc.VehicleTypeOuterClass.VehicleType;
import com.xabe.vehicle.api.grpc.WheelOuterClass.Wheel;
import com.xabe.vehicle.api.grpc.WheelTypeOuterClass.WheelType;
import com.xabe.vehicle.api.grpc.update.updatecarinfo.UpdateCarInfoRequestOuterClass.UpdateCarInfoRequest;
import com.xabe.vehicle.api.grpc.update.updatevehiclebasicinfo.UpdateVehicleBasicInfoRequest.UpdateVehicleBasicRequest;
import com.xabe.vehicle.api.grpc.update.updatevehicleecolor.UpdateVehicleColorRequestOuterClass.UpdateVehicleColorRequest;
import com.xabe.vehicle.api.grpc.update.updatevehicleecolor.UpdateVehicleColorRequestOuterClass.UpdateVehicleColorRequest.Color;
import com.xabe.vehicle.api.grpc.update.updatevehicleecolor.UpdateVehicleColorRequestOuterClass.UpdateVehicleColorRequest.I18NText;
import com.xabe.vehicle.api.grpc.update.updatevehicleengine.UpdateVehicleEngineRequestOuterClass.UpdateVehicleEngineRequest;
import com.xabe.vehicle.api.grpc.update.updatevehicleprice.UpdateVehiclePriceRequestOuterClass.UpdateVehiclePriceRequest;
import com.xabe.vehicle.api.grpc.update.updatevehicleprice.UpdateVehiclePriceRequestOuterClass.UpdateVehiclePriceRequest.Price;
import com.xabe.vehicle.api.grpc.update.updatevehicleprice.UpdateVehiclePriceRequestOuterClass.UpdateVehiclePriceRequest.Prices;
import com.xabe.vehicle.api.grpc.update.updatevehicletransmission.UpdateVehicleTransmissionRequestOuterClass.UpdateVehicleTransmissionRequest;
import com.xabe.vehicle.api.grpc.update.updatevehiclewheel.UpdateVehicleWheelRequestOuterClass.UpdateVehicleWheelRequest;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

@SpringBootTest(classes = App.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ExtendWith(SpringExtension.class)
@EnabledIfSystemProperty(named = "solr.data", matches = "true")
@ActiveProfiles("test")
public class LoadVehicleGrpcControllerIT {

    @Autowired
    protected HttpSolrClient solrClient;

    private final ExecutorService executors = Executors.newFixedThreadPool(10);

    private final String url = "https://parseapi.back4app.com/classes/Car_Model_List_%s?skip=%d&limit=%d";

    @Test
    public void loadMazda() throws Exception {
        //Given
        final HttpClient httpClient = this.createHttpClient();
        final var request = this
            .createHttpRequestGet("https://parseapi.back4app.com/classes/Car_Model_List_MAZDA?count=1&limit=0");

        //When
        final var response = httpClient.send(request, new JsonBodyHandlers<>(new TypeReference<CountResponse>() {
        }));

        //Then
        assertThat(response.statusCode(), is(200));

        final CountResponse count = response.body();

        final Pagination pagination = Pagination.of(50, count.getCount());

        final List<CompletableFuture<CarResponse>> completableFutures =
            StreamSupport.stream(new PaginationIterator(pagination).spliterator(), false)
                .map(this.callAsyncData(httpClient, "MAZDA"))
                .collect(Collectors.toList());

        final CompletableFuture<List<CarResponse>> listCompletableFuture = this.allOfOrException(completableFutures);
        final List<Car> httpResponses =
            listCompletableFuture.get(60, TimeUnit.SECONDS).stream().flatMap(item -> item.getCars().stream()).distinct()
                .collect(Collectors.toList());

        httpResponses.forEach(this::createCar);

        assertThat(httpResponses, is(notNullValue()));
        assertThat(httpResponses, is(hasSize(count.getCount())));
    }

    @Test
    public void loadBMW() throws Exception {
        //Given
        final HttpClient httpClient = this.createHttpClient();
        final var request = this
            .createHttpRequestGet("https://parseapi.back4app.com/classes/Car_Model_List_BMW?count=1&limit=0");

        //When
        final var response = httpClient.send(request, new JsonBodyHandlers<>(new TypeReference<CountResponse>() {
        }));

        //Then
        assertThat(response.statusCode(), is(200));

        final CountResponse count = response.body();

        final Pagination pagination = Pagination.of(50, count.getCount());

        final List<CompletableFuture<CarResponse>> completableFutures =
            StreamSupport.stream(new PaginationIterator(pagination).spliterator(), false)
                .map(this.callAsyncData(httpClient, "BMW"))
                .collect(Collectors.toList());

        final CompletableFuture<List<CarResponse>> listCompletableFuture = this.allOfOrException(completableFutures);
        final List<Car> httpResponses =
            listCompletableFuture.get(60, TimeUnit.SECONDS).stream().flatMap(item -> item.getCars().stream()).distinct()
                .collect(Collectors.toList());

        httpResponses.forEach(this::createCar);

        assertThat(httpResponses, is(notNullValue()));
        assertThat(httpResponses, is(hasSize(count.getCount())));
    }

    private void createCar(final Car car) {
        final UpdateVehicleBasicRequest updateVehicleBasicRequest = UpdateVehicleBasicRequest.newBuilder()
            .setId(VehicleId.newBuilder().setType(VehicleType.VEHICLE_CAR).setId(car.getId()).build())
            .setStoreId("sp")
            .setTimestamp(System.currentTimeMillis())
            .setBrand(car.getMake())
            .setCategory(car.getCategory())
            .setModel(car.getModel()).build();
        GrpcUtil.instance().getUpdateVehicleServiceBlockingStub().updateVehicleBasicInfo(updateVehicleBasicRequest);

        try {
            this.solrClient.commit();
        } catch (final Exception e) {
        }

        final UpdateVehicleEngineRequest updateVehicleEngineRequest =
            UpdateVehicleEngineRequest.newBuilder()
                .setId(VehicleId.newBuilder().setType(VehicleType.VEHICLE_CAR).setId(car.getId()).build())
                .setStoreId("sp")
                .setTimestamp(System.currentTimeMillis())
                .addAllEngines(List.of(
                    Engine.newBuilder().setFuel(FuelType.FUEL_HYDROGEN).setType(EngineType.ENGINE_FLAT).build()))
                .build();

        GrpcUtil.instance().getUpdateVehicleServiceBlockingStub().updateVehicleEngine(updateVehicleEngineRequest);

        try {
            this.solrClient.commit();
        } catch (final Exception e) {
        }

        final UpdateVehiclePriceRequest updateVehiclePriceRequest =
            UpdateVehiclePriceRequest.newBuilder()
                .setId(VehicleId.newBuilder().setType(VehicleType.VEHICLE_CAR).setId(car.getId()).build())
                .setStoreId("sp")
                .setTimestamp(System.currentTimeMillis())
                .setPrices(
                    Prices.newBuilder().setCurrencyCode("EUR")
                        .addAllPrices(List.of(Price.newBuilder().setPrice(100L).setDiscount(0).build()))
                        .build()).build();

        GrpcUtil.instance().getUpdateVehicleServiceBlockingStub().updateVehiclePrice(updateVehiclePriceRequest);

        try {
            this.solrClient.commit();
        } catch (final Exception e) {
        }

        final UpdateVehicleWheelRequest updateVehicleWheelRequest =
            UpdateVehicleWheelRequest.newBuilder()
                .setId(VehicleId.newBuilder().setType(VehicleType.VEHICLE_CAR).setId(car.getId()).build())
                .setStoreId("sp")
                .setTimestamp(System.currentTimeMillis())
                .addAllWheels(
                    List.of(Wheel.newBuilder().setSize(16).setType(WheelType.WHEEL_SUMMER).setCount(4).build()))
                .build();

        GrpcUtil.instance().getUpdateVehicleServiceBlockingStub().updateVehicleWheel(updateVehicleWheelRequest);

        try {
            this.solrClient.commit();
        } catch (final Exception e) {
        }

        final UpdateVehicleColorRequest updateVehicleColorRequest =
            UpdateVehicleColorRequest.newBuilder()
                .setId(VehicleId.newBuilder().setType(VehicleType.VEHICLE_CAR).setId(car.getId()).build())
                .setStoreId("sp")
                .setTimestamp(System.currentTimeMillis())
                .addAllColors(List.of(
                    Color.newBuilder().setId("1").setType(ColorType.COLOR_SOLID)
                        .addAllColorNames(List.of(I18NText.newBuilder().setLocale("es-ES").setText("red").build()))
                        .build()))
                .build();

        GrpcUtil.instance().getUpdateVehicleServiceBlockingStub().updateVehicleColor(updateVehicleColorRequest);

        try {
            this.solrClient.commit();
        } catch (final Exception e) {
        }

        final UpdateVehicleTransmissionRequest updateVehicleTransmissionRequest =
            UpdateVehicleTransmissionRequest.newBuilder()
                .setId(VehicleId.newBuilder().setType(VehicleType.VEHICLE_CAR).setId(car.getId()).build())
                .setStoreId("sp")
                .setTimestamp(System.currentTimeMillis())
                .addAllTransmissions(
                    List.of(TransmissionType.TRANSMISSION_AUTOMATIC, TransmissionType.TRANSMISSION_MANUAL)).build();

        GrpcUtil.instance().getUpdateVehicleServiceBlockingStub()
            .updateVehicleTransmission(updateVehicleTransmissionRequest);

        try {
            this.solrClient.commit();
        } catch (final Exception e) {
        }

        final UpdateCarInfoRequest updateCarInfoRequest =
            UpdateCarInfoRequest.newBuilder()
                .setId(VehicleId.newBuilder().setType(VehicleType.VEHICLE_CAR).setId(car.getId()).build())
                .setStoreId("sp")
                .setTimestamp(System.currentTimeMillis())
                .setDoors(4).setType(CarType.CAR_HATCHBACK).build();

        GrpcUtil.instance().getUpdateVehicleServiceBlockingStub().updateCarInfo(updateCarInfoRequest);

        try {
            this.solrClient.commit();
        } catch (final Exception e) {
        }
    }

    private Function<Pagination, CompletableFuture<CarResponse>> callAsyncData(final HttpClient httpClient,
        final String make) {
        return (pagination) -> httpClient
            .sendAsync(
                this.createHttpRequestGet(String.format(this.url, make, pagination.getSkip(), pagination.getLimit())),
                new JsonBodyHandlers<>(new TypeReference<CarResponse>() {
                }))
            .thenApply(HttpResponse::body)
            .whenComplete((r, t) -> {
                System.out.println("Number items " + r.getCars().size());
            });
    }

    private HttpRequest createHttpRequestGet(final String uri) {
        System.out.println("call " + uri);
        return HttpRequest
            .newBuilder(URI.create(uri))
            .timeout(Duration.ofMillis(5000))
            .GET()
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .setHeader("X-Parse-Application-Id", "hlhoNKjOvEhqzcVAJ1lxjicJLZNVv36GdbboZj3Z")
            .setHeader("X-Parse-Master-Key", "SNMJJF0CZZhTPhLDIqGhTlUNV9r60M2Z5spyWfXW")
            .build();
    }

    private HttpClient createHttpClient() {
        return HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)  // this is the default
            .connectTimeout(Duration.ofMillis(5000))
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .executor(this.executors)
            .build();
    }

    private <T> CompletableFuture<List<T>> allOfOrException(final Collection<CompletableFuture<T>> futures) {
        final CompletableFuture<List<T>> result = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApplyAsync(items -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()),
                this.executors);

        for (final CompletableFuture<?> f : futures) {
            f.handle((c, ex) -> ex == null || result.completeExceptionally(ex));
        }
        return result;
    }

}

