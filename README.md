# Weatherman

A REST API that forecasts temperatures for cities using [Open Weather API](https://openweathermap.org/forecast5). This service is an integration layer between mobile apps and the 3rd-party API.

## Calculating average temperature

[5-day-3-hour Forecast API](https://openweathermap.org/forecast5) returns 40 3-hour timestamps starting after the current hour. The timestamps spreads at the hours 0, 3, 6, 9, 12, 15, 18, and 21.

To calculate accurate average temperature, weatherman skips the days that have the number of timestamps less than 8 and group the timestamps by day. For the best case, we have 5 days. For the worst case, we have 4 days.

For example, if the forecast response contains "2022-11-05 21:00", "2022-11-06 00:00", "2022-11-06 03:00", "2022-11-06 06:00", "2022-11-06 09:00", "2022-11-06 12:00", "2022-11-06 15:00", "2022-11-06 18:00", "2022-11-06 21:00", "2022-11-07 00:00", only 8 timestamps of the "2022-11-06" will be kept, the rest will be omitted.

## Free Plan Rate Limiting

Open Weather API restricts [1,000,000 calls per month and 60 calls per minute](https://openweathermap.org/price). If multiple users kept calling weatherman day by day, the rate limit would be reached.

### Solutions

#### 1) Cache daily forecast by city

Weatherman breaks down the response of [5-day-3-hour Forecast API](https://openweathermap.org/forecast5) into daily forecasts and cache them into Redis. This way the forecast caches can be retrieved if the next requests ask for the same city in any day of the same five days. This caching helps reduce calls to Open Weather API at the expense of less accurate forecast.

#### 2) Rotate API keys

If the quota of Open Weather API is nearly reached, weatherman will switch to another configured API key. Moving on with different API keys avoids exceeding Open Weather API's quota.

For example, if the configured API keys are "foo" and "bar", when the quota of key "foo" is reached, weatherman will use the key "bar" to keep calling the API.

#### 3) Retry and fallback

If Open Weather API returns Too Many Request (429), weatherman will attempt to call the API for a couple of times before switching to the next API key.

#### 4) Rate-limit by user such as IP address

To lower the probability of exceeding the quota and protect weatherman from being DDOS, a rate limiter allows only two requests for a second by a single IP address. This rate limit makes sense because a user shouldn't call twice to get the less frequently changed data within a second.

#### 5) Convert temperature by code

Instead of sending different calls to Open Weather API for Celsius and Fahrenheit, weatherman caches only Celsius data before converting it to Fahrenheit on demand. This method reduces half of the requests to Open Weather API if users retrieve data for the same city with different units.

## Forecast API

### 1) Get tomorrow temperature for cities

```
GET /weather/summary
```

#### Request parameters (required)

| Parameters    | Description                                             | Type                      |
|---------------|---------------------------------------------------------|---------------------------|
| `cities`      | List of cityIds                                         | List                      |
| `temperature` | Cities' temperatures should be above this `temperature` | Double                    |
| `unit`        | Temperature measurement unit                            | `celsius` or `fahrenheit` |

#### Example

##### Get cities whose temperature is higher than 9 degree Celsius

```sh
curl --location --request GET 'http://localhost:8080/weather/summary?cities=2618425,3621849,3133880&temperature=9&unit=celsius'
```

##### Response

```json
[
  {
    "id": "2618425",
    "name": "Copenhagen",
    "averageTemperature": 13.11,
    "date": "2022-11-11"
  },
  {
    "id": "3621849",
    "name": "San José",
    "averageTemperature": 19.28,
    "date": "2022-11-11"
  },
  {
    "id": "3133880",
    "name": "Trondheim",
    "averageTemperature": 9.1,
    "date": "2022-11-11"
  }
]
```

##### Get a city whose temperature is higher than 40 degree Fahrenheit

```sh
curl --location --request GET 'http://localhost:8080/weather/summary?cities=2618425&temperature=40&unit=fahrenheit'
```

##### Response

```json
[
  {
    "id": "2618425",
    "name": "Copenhagen",
    "averageTemperature": 55.598,
    "date": "2022-11-11"
  }
]
```

### 2) Get temperatures for the next 5 days for a single city

```
GET /weather/cities/<cityId>
```

#### Path parameters

| Parameters | Description | Type   |
|------------|-------------|--------|
| `cityId`   | City ID     | String |

#### Example

##### Get temperatures for the next 4 days for a single city

```sh
curl --location --request GET 'http://localhost:8080/weather/cities/3621849'
```

##### Response

```json
[
  {
    "id": "3621849",
    "name": "San José",
    "averageTemperature": 19.28,
    "date": "2022-11-11"
  },
  {
    "id": "3621849",
    "name": "San José",
    "averageTemperature": 19.31,
    "date": "2022-11-12"
  },
  {
    "id": "3621849",
    "name": "San José",
    "averageTemperature": 19.09,
    "date": "2022-11-13"
  },
  {
    "id": "3621849",
    "name": "San José",
    "averageTemperature": 18.63,
    "date": "2022-11-14"
  }
]
```

## Quickstart

At project directory, run this command to set up Redis.

```sh
docker compose up -d
```

In IntelliJ, start `WeathermanApp`.

Optionally, you can pass `spring.profiles.active=dev` to use the real use-case rate limiting.

## Resources

Data of cities can be found at [Bulk Download](http://bulk.openweathermap.org/sample/).