package com.example.ex2.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ex2.R;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Service implements Serializable {
    private String serviceName;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    private String serviceId;

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    private String producer;

    public String getServiceCurrency() {
        return serviceCurrency;
    }

    public void setServiceCurrency(String serviceCurrency) {
        this.serviceCurrency = serviceCurrency;
    }

    private String serviceCurrency;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setAvailability(Map<String, Range> availability) {
        this.availability = availability;
    }

    private Double price;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDuration() {
        return this.duration;
    }

    public void setDurationInMinutes(String durationInMinutes) {
        this.duration = durationInMinutes;
    }

    private String duration;

    public Map<String, Range> getAvailability() {
        return availability;
    }

    private Map<String, Range> availability;

    public Service()
    {

    }

    public Service(String serviceName, String durationInMinutes, Map<String, Range> availability, Double price, String currency) {
        this.serviceName = serviceName;
        this.duration = durationInMinutes;
        this.availability = availability;
        this.price = price;
        this.serviceCurrency = currency;
    }

    public Service(String serviceName, String duration) {
        this.serviceName = serviceName;
        this.duration = duration;
        this.availability = new HashMap<String, Range>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean containsKey(@Nullable Object o) {
                return false;
            }

            @Override
            public boolean containsValue(@Nullable Object o) {
                return false;
            }

            @Nullable
            @Override
            public Range get(@Nullable Object o) {
                return null;
            }

            @Nullable
            @Override
            public Range put(String s, Range range) {
                return null;
            }

            @Nullable
            @Override
            public Range remove(@Nullable Object o) {
                return null;
            }

            @Override
            public void putAll(@NonNull Map<? extends String, ? extends Range> map) {

            }

            @Override
            public void clear() {

            }

            @NonNull
            @Override
            public Set<String> keySet() {
                return Collections.emptySet();
            }

            @NonNull
            @Override
            public Collection<Range> values() {
                return Collections.emptyList();
            }

            @NonNull
            @Override
            public Set<Entry<String, Range>> entrySet() {
                return Collections.emptySet();
            }
        };

        this.availability.put("sunday", new Range());
        this.availability.put("monday", new Range());
        this.availability.put("tuesday", new Range());
        this.availability.put("wednesday", new Range());
        this.availability.put("thursday", new Range());
        this.availability.put("friday", new Range());
        this.availability.put("saturday", new Range());
    }
}
