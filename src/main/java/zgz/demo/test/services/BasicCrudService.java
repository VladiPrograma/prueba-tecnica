package zgz.demo.test.services;

import java.util.List;

public interface BasicCrudService<Request, Response, ID> {

  Response create(Request request);

  List<Response> findAll();

  Response update(ID id, Request request);

  void delete(ID id);

}
