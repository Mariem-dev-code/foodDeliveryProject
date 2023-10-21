import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IMenu } from '../menu.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../menu.test-samples';

import { MenuService } from './menu.service';

const requireRestSample: IMenu = {
  ...sampleWithRequiredData,
};

describe('Menu Service', () => {
  let service: MenuService;
  let httpMock: HttpTestingController;
  let expectedResult: IMenu | IMenu[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(MenuService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Menu', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const menu = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(menu).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Menu', () => {
      const menu = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(menu).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Menu', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Menu', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Menu', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addMenuToCollectionIfMissing', () => {
      it('should add a Menu to an empty array', () => {
        const menu: IMenu = sampleWithRequiredData;
        expectedResult = service.addMenuToCollectionIfMissing([], menu);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(menu);
      });

      it('should not add a Menu to an array that contains it', () => {
        const menu: IMenu = sampleWithRequiredData;
        const menuCollection: IMenu[] = [
          {
            ...menu,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMenuToCollectionIfMissing(menuCollection, menu);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Menu to an array that doesn't contain it", () => {
        const menu: IMenu = sampleWithRequiredData;
        const menuCollection: IMenu[] = [sampleWithPartialData];
        expectedResult = service.addMenuToCollectionIfMissing(menuCollection, menu);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(menu);
      });

      it('should add only unique Menu to an array', () => {
        const menuArray: IMenu[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const menuCollection: IMenu[] = [sampleWithRequiredData];
        expectedResult = service.addMenuToCollectionIfMissing(menuCollection, ...menuArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const menu: IMenu = sampleWithRequiredData;
        const menu2: IMenu = sampleWithPartialData;
        expectedResult = service.addMenuToCollectionIfMissing([], menu, menu2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(menu);
        expect(expectedResult).toContain(menu2);
      });

      it('should accept null and undefined values', () => {
        const menu: IMenu = sampleWithRequiredData;
        expectedResult = service.addMenuToCollectionIfMissing([], null, menu, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(menu);
      });

      it('should return initial array if no Menu is added', () => {
        const menuCollection: IMenu[] = [sampleWithRequiredData];
        expectedResult = service.addMenuToCollectionIfMissing(menuCollection, undefined, null);
        expect(expectedResult).toEqual(menuCollection);
      });
    });

    describe('compareMenu', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMenu(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareMenu(entity1, entity2);
        const compareResult2 = service.compareMenu(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareMenu(entity1, entity2);
        const compareResult2 = service.compareMenu(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareMenu(entity1, entity2);
        const compareResult2 = service.compareMenu(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
