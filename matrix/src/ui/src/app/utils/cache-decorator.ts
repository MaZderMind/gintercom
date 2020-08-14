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
