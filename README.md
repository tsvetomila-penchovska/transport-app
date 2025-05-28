# Transport Application

## Prerequisites
The following environment variables are expected to be provided for a successful authentication:
`CLIENT_ID` and `CLIENT_SECRET`

## Notes, assumptions and future improvements

- All configuration properties are placed in the main method for simplicity.
- The `type` property in `Transport` object is defined as `Enum` by an assumption. 
If that is not required, it can be simply converted to `String`.
- The `transporterid` property of the transports objects is `null` and was not specified that needs to be printed in the console,
so it is omitted.
- Simple error handling is implemented, with basic custom exceptions. These can be extended if more fine-grain exception handling is required.
- Only 'happy path' unit tests are added. These can be extended to cover edge cases.