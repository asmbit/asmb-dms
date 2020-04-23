package asmb.dms.test

import static groovy.test.GroovyAssert.*

import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters

import asmb.dms.DMS
import asmb.dms.Utils
import asmb.dms.api.Tasks.State

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class DmsTestCase {

	Random random = new Random()

	@Test
	void test1People() {

		def dms = new DMS("admin")
		def response

		response = dms.people().get()
		//println Utils.toPrettyPrint(response.body)
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertTrue(response.body.list.entries.entry.id.contains("admin"))
		assertTrue(response.body.list.entries.entry.id.contains("afrena"))
		assertTrue(response.body.list.entries.entry.id.contains("werner"))

		response = dms.groups().get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertTrue(response.body.list.entries.entry.id.contains("GROUP_TEST"))

		response = dms.people('admin').get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertTrue(response.body.entry.capabilities.isAdmin)

		response = dms.people('afrena').groups().get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertTrue(response.body.list.entries.entry.id.contains("GROUP_TEST"))

		response = dms.groups("GROUP_TEST").get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertEquals("GROUP_TEST", response.body.entry.id)

		response = dms.groups("GROUP_TEST").members().get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertEquals(2, response.body.list.entries.entry.size())
		assertTrue(response.body.list.entries.entry.id.contains("afrena"))
		assertTrue(response.body.list.entries.entry.id.contains("werner"))

		response = dms.people("-me-").favorites().get()
		assertTrue(response.success)

	}

	@Test
	void test2Nodes() {

		def dms = new DMS("admin")
		def folderName = "test" + random.nextInt(99999)
		def file = new File(getClass().getResource('/content/file.txt').toURI())
		def response, value, nodeId, folderId

		response = dms.nodes("-root-").get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertEquals("Company Home", response.body.entry.name)
		assertTrue(response.body.entry.isFolder)
		assertEquals("cm:folder", response.body.entry.nodeType)

		response = dms.nodes("/").get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertEquals("Company Home", response.body.entry.name)
		assertTrue(response.body.entry.isFolder)
		assertEquals("cm:folder", response.body.entry.nodeType)

		response = dms.nodes("/").get([include: 'association, permissions, path'])
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertTrue(response.body.entry.containsKey('permissions'))

		response = dms.nodes("/").children().get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertTrue(response.body.list.entries.entry.name.contains("Data Dictionary"))

		response = dms.nodes("-shared-").get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertEquals("Shared", response.body.entry.name)
		assertEquals("cm:folder", response.body.entry.nodeType)
		nodeId = response.body.entry.id

		response = dms.nodes("-shared-").children().post(folderName, "cm:folder")
		assertTrue(response.success)
		assertEquals(201, response.code)
		assertEquals("cm:folder", response.body.entry.nodeType)
		folderId = response.body.entry.id

		response = dms.nodes(folderId).children().post(file)
		assertTrue(response.success)
		assertEquals(201, response.code)
		assertEquals("file.txt", response.body.entry.name)
		assertEquals("cm:content", response.body.entry.nodeType)
		assertEquals(folderId, response.body.entry.parentId)
		nodeId = response.body.entry.id

		response = dms.nodes("/Shared/$folderName/file.txt").get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertEquals(nodeId, response.body.entry.id)

		response = dms.nodes(nodeId).content().get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertEquals(response.byteStream.text, file.text)
		response.close()

		response = dms.nodes(nodeId).delete()
		assertTrue(response.success)
		assertEquals(204, response.code)

		response = dms.nodes(nodeId).get()
		assertTrue(!response.success)
		assertEquals(404, response.code)

		response = dms.nodes(folderId).children().post(file, "file2.txt", "cm:content", ["cm:title": "Title 2", "cm:description": "Description 2"])
		assertTrue(response.success)
		assertEquals("file2.txt", response.body.entry.name)
		assertEquals("cm:content", response.body.entry.nodeType)
		assertEquals("Title 2", response.body.entry.properties["cm:title"])
		assertEquals("Description 2", response.body.entry.properties["cm:description"])
		nodeId = response.body.entry.id

		response = dms.nodes(nodeId).put(properties: ["cm:title": "Title 3", "cm:description": "Description 3"])
		assertTrue(response.success)
		assertEquals("Title 3", response.body.entry.properties["cm:title"])
		assertEquals("Description 3", response.body.entry.properties["cm:description"])

		response = dms.nodes(nodeId).delete()
		assertTrue(response.success)
		assertEquals(204, response.code)

		response = dms.nodes(folderId).children().post("file3.txt", "cm:content")
		assertTrue(response.success)
		assertEquals(201, response.code)
		assertEquals("cm:content", response.body.entry.nodeType)
		nodeId = response.body.entry.id

		response = dms.nodes(nodeId).content().put(file)
		assertTrue(response.success)
		assertEquals("file3.txt", response.body.entry.name)
		assertEquals("cm:content", response.body.entry.nodeType)
		nodeId = response.body.entry.id

		response = dms.nodes(nodeId).content().get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertEquals(response.byteStream.text, file.text)
		response.close()

		response = dms.nodes(nodeId).delete()
		assertTrue(response.success)
		assertEquals(204, response.code)

		response = dms.nodes(nodeId).get()
		assertTrue(!response.success)
		assertEquals(404, response.code)

		response = dms.nodes(folderId).delete()
		assertTrue(response.success)
		assertEquals(204, response.code)

		response = dms.nodes(folderId).get()
		assertTrue(!response.success)
		assertEquals(404, response.code)
	}

	@Test
	void test3Comments() {

		def dms = new DMS("admin")
		def response, fileId

		response = dms.nodes("-shared-").children().post("test" + random.nextInt(99999), "cm:content")
		assertTrue(response.success)
		assertEquals(201, response.code)
		assertEquals("cm:content", response.body.entry.nodeType)
		fileId = response.body.entry.id

		response = dms.nodes(fileId).comments().post("test1")
		assertTrue(response.success)
		assertEquals(201, response.code)
		assertEquals("test1", response.body.entry.content)

		response = dms.nodes(fileId).comments().get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertEquals(1, response.body.list.entries.entry.size())
		assertEquals("test1", response.body.list.entries.entry.content[0])

		response = dms.nodes(fileId).delete()
		assertTrue(response.success)
		assertEquals(204, response.code)
	}


	@Test
	void test4Associations() {

		def dms = new DMS("afrena")
		def image = new File(getClass().getResource('/content/asmb.gif').toURI())
		def response, folder, company, gadget, review, file1, file2

		response = dms.nodes("-shared-").children().post("test" + random.nextInt(99999), "cm:folder")
		assertTrue(response.success)
		assertEquals(201, response.code)
		assertEquals("cm:folder", response.body.entry.nodeType)
		folder = response.body.entry.id

		response = dms.nodes(folder).children().post("company", "fdk:company", ["fdk:email": "mail@asmb.it", "fdk:url": "http://asmb.it", "fdk:city": "Brixen"])
		assertTrue(response.success)
		assertEquals(201, response.code)
		assertEquals("fdk:company", response.body.entry.nodeType)
		company = response.body.entry.id

		response = dms.nodes(folder).children().post("review", "cm:content", null)
		assertTrue(response.success)
		assertEquals(201, response.code)
		assertEquals("cm:content", response.body.entry.nodeType)
		review = response.body.entry.id

		response = dms.nodes(folder).children().post(image, "image1.gif", "cm:content", null)
		assertTrue(response.success)
		assertEquals(201, response.code)
		assertEquals("cm:content", response.body.entry.nodeType)
		file1 = response.body.entry.id

		response = dms.nodes(folder).children().post(image, "image2.gif", "cm:content", null)
		assertTrue(response.success)
		assertEquals(201, response.code)
		assertEquals("cm:content", response.body.entry.nodeType)
		file2 = response.body.entry.id

		response = dms.nodes(folder).children().post(name: "gadget", nodeType: "fdk:gadget", "secondaryChildren": [["childId": file1, "assocType": "fdk:images"], ["childId": file2, "assocType": "fdk:images"]], "targets": [["targetId": review, "assocType": "fdk:reviews"], ["targetId": company, "assocType": "fdk:company"]])
		assertTrue(response.success)
		assertEquals(201, response.code)
		assertEquals("fdk:gadget", response.body.entry.nodeType)
		gadget = response.body.entry.id

		response = dms.nodes(gadget).targets().get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertTrue(response.body.list.entries.entry.id.contains(review))
		assertTrue(response.body.list.entries.entry.name.contains("review"))

		response = dms.nodes(gadget).targets(review).delete()
		assertTrue(response.success)
		assertEquals(204, response.code)

		response = dms.nodes(gadget).targets(review).delete()
		assertTrue(!response.success)
		assertEquals(404, response.code)

		response = dms.nodes(gadget).targets().post(review, "fdk:reviews")
		assertTrue(response.success)
		assertEquals(201, response.code)

		response = dms.nodes(gadget).targets().post(review, "fdk:reviews")
		assertTrue(!response.success)
		assertEquals(409, response.code)

		response = dms.nodes(folder).delete()
		assertTrue(response.success)
		assertEquals(204, response.code)
	}

	@Test
	void test5Processes() {

		def dms = new DMS("admin")
		def response, processId, folderId, fileId, i

		response = dms.nodes("-shared-").children().post("test" + random.nextInt(99999), "cm:folder")
		assertTrue(response.success)
		assertEquals(201, response.code)
		assertEquals("cm:folder", response.body.entry.nodeType)
		folderId = response.body.entry.id

		response = dms.nodes(folderId).children().post(new File(getClass().getResource('/content/file.txt').toURI()))
		assertTrue(response.success)
		assertEquals(201, response.code)
		assertEquals(folderId, response.body.entry.parentId)
		fileId = response.body.entry.id

		response = dms.processes().post("activitiAdhoc", "admin")
		assertTrue(response.success)
		assertEquals(201, response.code)
		assertEquals("activitiAdhoc", response.body.entry.processDefinitionKey)
		assertEquals("admin", response.body.entry.startUserId)
		assertNotNull(response.body.entry.id)
		processId = response.body.entry.id

		response = dms.processes().get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertTrue(response.body.list.entries.entry.id.contains(processId))

		response = dms.processes().get(where: '(processDefinitionKey  = activitiAdhoc)')
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertTrue(response.body.list.entries.entry.id.contains(processId))

		response = dms.processes().get(where: '(startUserId = admin)')
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertTrue(response.body.list.entries.entry.id.contains(processId))

		response = dms.processes().get(where: '(status = active)')
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertTrue(response.body.list.entries.entry.id.contains(processId))

		response = dms.processes().get(where: "(variables/bpm_priority >= 'd:int 1')")
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertTrue(response.body.list.entries.entry.id.contains(processId))

		response = dms.processes(processId).tasks().get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertEquals(["admin"], response.body.list.entries.entry.assignee)
		assertEquals([processId], response.body.list.entries.entry.processId)

		response = dms.processes(processId).variables().get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		i = response.body.list.entries.entry.name.indexOf("bpm_assignee")
		assertEquals("admin", response.body.list.entries.entry[i].value)

		response = dms.processes(processId).variables().post("test1", "123", "d:int")
		assertTrue(response.success)
		assertEquals(201, response.code)
		assertEquals("test1", response.body.entry.name)
		assertEquals(123, response.body.entry.value)
		assertEquals("d:int", response.body.entry.type)

		response = dms.processes(processId).variables().get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		i = response.body.list.entries.entry.name.indexOf("test1")
		assertEquals(response.body.list.entries.entry[i].value, 123)

		response = dms.processes(processId).variables().post("test2", "123", "d:text")
		assertTrue(response.success)
		assertEquals(201, response.code)
		assertEquals("test2", response.body.entry.name)
		assertEquals("123", response.body.entry.value)
		assertEquals("d:text", response.body.entry.type)

		response = dms.processes(processId).variables().get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		i = response.body.list.entries.entry.name.indexOf("test2")
		assertEquals("123", response.body.list.entries.entry[i].value)

		response = dms.processes(processId).items().post(fileId)
		assertTrue(response.success)
		assertEquals(201, response.code)
		assertEquals(fileId, response.body.entry.id)

		response = dms.processes(processId).items().get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertTrue(response.body.list.entries.entry.id.contains(fileId))

		response = dms.processes(processId).delete()
		assertTrue(response.success)
		assertEquals(204, response.code)

		response = dms.nodes(fileId).delete()
		assertTrue(response.success)
		assertEquals(204, response.code)

		response = dms.nodes(folderId).delete()
		assertTrue(response.success)
		assertEquals(204, response.code)
	}

	@Test
	void test6Tasks() {

		def dms = new DMS("admin")
		def response, processId, taskId, i

		response = dms.processes().post("activitiAdhoc", "afrena")
		assertTrue(response.success)
		assertEquals(201, response.code)
		assertEquals("activitiAdhoc", response.body.entry.processDefinitionKey)
		assertEquals("admin", response.body.entry.startUserId)
		processId = response.body.entry.id

		response = dms.processes(processId).tasks().get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertEquals(1, response.body.list.entries.entry.size())
		taskId = response.body.list.entries.entry.id[0]

		response = dms.tasks().get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertTrue(response.body.list.entries.entry.id.contains(taskId))

		response = dms.tasks(taskId).get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertEquals("adhocTask", response.body.entry.activityDefinitionId)
		assertEquals("afrena", response.body.entry.assignee)
		assertEquals("claimed", response.body.entry.state)

		dms = new DMS("afrena")

		response = dms.tasks().get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertTrue(response.body.list.entries.entry.id.contains(taskId))

		response = dms.tasks(taskId).put(State.COMPLETED)
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertEquals("completed", response.body.entry.state)

		dms = new DMS("admin")

		response = dms.processes(processId).tasks().get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		taskId = response.body.list.entries.entry.id[0]

		response = dms.tasks(taskId).get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertEquals("verifyTaskDone", response.body.entry.activityDefinitionId)
		assertEquals("admin", response.body.entry.assignee)
		assertEquals("claimed", response.body.entry.state)

		response = dms.tasks(taskId).put(State.COMPLETED)
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertEquals("completed", response.body.entry.state)

		response = dms.tasks(taskId).get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertEquals("completed", response.body.entry.state)

		response = dms.processes(processId).get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertTrue(response.body.entry.completed)

		response = dms.processes(processId).tasks().get(status: 'completed')
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertEquals(2, response.body.list.entries.entry.size())

		response = dms.processes(processId).tasks().get(status: 'active')
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertEquals(0, response.body.list.entries.entry.size())
	}

	@Test
	void test7Tags() {

		def dms = new DMS("admin")
		def folderName = "test" + random.nextInt(99999)
		def file = new File(getClass().getResource('/content/file.txt').toURI())
		def response, value, nodeId, folderId, tagId

		response = dms.nodes("-shared-").get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertEquals("Shared", response.body.entry.name)
		assertEquals("cm:folder", response.body.entry.nodeType)
		nodeId = response.body.entry.id

		response = dms.nodes("-shared-").children().post(folderName, "cm:folder")
		assertTrue(response.success)
		assertEquals(201, response.code)
		assertEquals("cm:folder", response.body.entry.nodeType)
		folderId = response.body.entry.id

		response = dms.nodes(folderId).children().post(file)
		assertTrue(response.success)
		assertEquals(201, response.code)
		assertEquals("file.txt", response.body.entry.name)
		assertEquals("cm:content", response.body.entry.nodeType)
		assertEquals(folderId, response.body.entry.parentId)
		nodeId = response.body.entry.id

		response = dms.nodes(nodeId).tags().post('test1')
		assertTrue(response.success)
		assertEquals(201, response.code)

		response = dms.nodes(nodeId).tags().post('test2')
		assertTrue(response.success)
		assertEquals(201, response.code)

		response = dms.nodes(nodeId).tags().get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertTrue(response.body.list.entries.entry.tag.contains('test1'))
		assertTrue(response.body.list.entries.entry.tag.contains('test2'))
		assertEquals(2, response.body.list.entries.entry.size())
		tagId = response.body.list.entries.entry[0].id

		response = dms.nodes(nodeId).tags(tagId).delete()
		assertTrue(response.success)
		assertEquals(204, response.code)

		response = dms.nodes(nodeId).tags().get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertFalse(response.body.list.entries.entry.tag.contains('test1'))
		assertTrue(response.body.list.entries.entry.tag.contains('test2'))
		assertEquals(1, response.body.list.entries.entry.size())
		tagId = response.body.list.entries.entry[0].id

		response = dms.nodes(nodeId).tags(tagId).delete()
		assertTrue(response.success)
		assertEquals(204, response.code)

		response = dms.nodes(nodeId).tags().get()
		assertTrue(response.success)
		assertEquals(200, response.code)
		assertFalse(response.body.list.entries.entry.tag.contains('test2'))
		assertEquals(0, response.body.list.entries.entry.size())

		response = dms.nodes(nodeId).delete()
		assertTrue(response.success)
		assertEquals(204, response.code)

		response = dms.nodes(folderId).delete()
		assertTrue(response.success)
		assertEquals(204, response.code)
	}
}
