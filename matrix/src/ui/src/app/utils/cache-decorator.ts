/**
 * Debounce a method
 *
 * class SearchBarComponent {
 *   constructor(private httpClient: HttpClient) {
 *   }
 *
 *   @Cache(50)
 *   getFoo(arg: bar) {
 *       return this.httpClient.get("/api/search?q=" + searchText).toPromise();
 *   }
 * }
 * </code>
 */
import * as _ from 'lodash';

interface CacheEntry {
  callArgs: IArguments;
  result: any;
}

const DEFAULT_CACHE_DURATION = 150;

/**
 * Cache-Decorator for Service-Methods
 *
 * By Applying this Decorator to a get*-Method of a Service-Class, especially such services that hit a HTTP-Backend with one or more
 * Requests per Call, the Result of the Get-Operation still stay valid for the given amount of time. This results in the HTTP_Call only
 * being executed once, even if the get-Method is invoked multiple times within the given maxAge.
 *
 * This happens often, when Components loading auxiliary Data (ie a Select-Box that loads the Set of available Options) are used
 * multiple times within the same Widget. The Cache-Decorator reduces the number of Calls to 1 while still alowing the Components to
 * request their auxiliary Data themself.
 *
 * The Cache-Decorator takes the given Arguments into account and stores the Result only once for each unique combination of arguments.
 * It is therefore safe to use the Decorator on a get+-Method that takes one or more Arguments.
 *
 * The Cache-Decorator works with native Result-Types and Object-Type as well as with Promises and Subscriptions, as long as the
 * Subscriptions are shared by calling `.pipe(share())` on them.
 *
 * @param maxAgeMs Amount of Time the Method-Result is valid (given in milliseconds)
 */
export function Cache(maxAgeMs = DEFAULT_CACHE_DURATION) {
  const cache: CacheEntry[] = [];

  return (target: any, key: any, descriptor: any) => {
    const oldFunc = descriptor.value;

    descriptor.value = function() {
      const cachedEntry = cache.find(cacheEntry =>
        _.isEqual(cacheEntry.callArgs, arguments));

      if (cachedEntry) {
        return cachedEntry.result;
      }

      const executionResult = oldFunc.apply(this, arguments);

      const newCacheEntry = {callArgs: arguments, result: executionResult};
      cache.push(newCacheEntry);

      setTimeout(() => {
        _.pull(cache, newCacheEntry);
      }, maxAgeMs);

      return executionResult;
    };
  };
}
