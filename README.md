# In progress...

## RFC 9457: Documented API Errors

### What is RFC 9457

RFC 9457 is proposed standard for API errors. 
Detailed documentation can be found [here](https://www.rfc-editor.org/rfc/rfc9457.html).

### Currency Not Found Error

This error can be returned when currency wasn't found.

### Invalid Currency Error

This error can be returned when currency's name or alphabetical code are invalid.

Currency's name must exist and be 1 - 64 characters long.
Currency's alphabetical code is optional, but if included, it must conform to ISO 4217.

### Exception Error

This error is a propagated generic error that is returned as HTTP 500.

The exact cause or details may not be provided. It is used as the last resort.
