import {Cache} from 'src/app/utils/cache-decorator';
import {Observable, of} from 'rxjs';
import {delay, share} from 'rxjs/operators';
import {fakeAsync, flush, tick} from '@angular/core/testing';

class ExampleService {
  calls: string[] = [];

  @Cache(20)
  literalGet(id: string): string {
    this.calls.push('call-' + id);
    return 'result-of-' + id;
  }

  @Cache(20)
  promiseGet(id: string): Promise<string> {
    this.calls.push('call-' + id);
    return of('result-of-' + id)
      .pipe(delay(150))
      .toPromise();
  }

  @Cache(20)
  subscriptionGet(id: string): Observable<string> {
    this.calls.push('call-' + id);
    return of('result-of-' + id)
      .pipe(share(), delay(150));
  }
}

describe('cache-decorator', () => {
  let exampleService: ExampleService;

  beforeEach(() => {
    exampleService = new ExampleService();
  });

  it('does only execute once within the cache-timeout', fakeAsync(() => {
    exampleService.literalGet('foo');
    exampleService.literalGet('foo');
    exampleService.literalGet('foo');

    expect(exampleService.calls).toEqual(['call-foo']);

    flush();
  }));

  it('executes once for each argument', fakeAsync(() => {
    const a = exampleService.literalGet('foo');
    const b = exampleService.literalGet('bar');
    const c = exampleService.literalGet('foo');

    expect(a).toEqual('result-of-foo');
    expect(b).toEqual('result-of-bar');
    expect(c).toEqual('result-of-foo');

    expect(exampleService.calls).toEqual(['call-foo', 'call-bar']);

    flush();
  }));

  it('re-executes after the cache-timeout', fakeAsync(() => {
    exampleService.literalGet('foo');
    exampleService.literalGet('bar');
    expect(exampleService.calls).toEqual(['call-foo', 'call-bar']);

    tick(15);
    exampleService.literalGet('foo');
    expect(exampleService.calls).toEqual(['call-foo', 'call-bar']);

    tick(15);
    exampleService.literalGet('foo');
    expect(exampleService.calls).toEqual(['call-foo', 'call-bar', 'call-foo']);

    flush();
  }));
  describe('with literal return-value', () => {
    it('returns the literal result on each call', fakeAsync(() => {
      const a = exampleService.literalGet('foo');
      const b = exampleService.literalGet('foo');
      const c = exampleService.literalGet('foo');

      expect(a).toEqual('result-of-foo');
      expect(b).toEqual('result-of-foo');
      expect(c).toEqual('result-of-foo');

      flush();
    }));
  });

  describe('with promise return-value', () => {
    it('returns the promise result on each call', fakeAsync(async () => {
      const a = exampleService.promiseGet('foo');
      const b = exampleService.promiseGet('foo');
      const c = exampleService.promiseGet('foo');

      tick(200);

      expect(await a).toEqual('result-of-foo');
      expect(await b).toEqual('result-of-foo');
      expect(await c).toEqual('result-of-foo');

      flush();
    }));
  });

  describe('with subscription return-value', () => {
    it('returns the promise result on each call', fakeAsync(async () => {
      const a = exampleService.subscriptionGet('foo');
      const b = exampleService.subscriptionGet('foo');
      const c = exampleService.subscriptionGet('foo');

      a.subscribe(ar => expect(ar).toEqual('result-of-foo'));
      b.subscribe(br => expect(br).toEqual('result-of-foo'));
      c.subscribe(cr => expect(cr).toEqual('result-of-foo'));

      tick(200);

      flush();
    }));
  });
});
